package com.stoloto.vip.service.balance;

import com.stoloto.vip.api.dto.BalanceDto;
import com.stoloto.vip.domain.entity.Transaction;
import com.stoloto.vip.domain.entity.User;
import com.stoloto.vip.domain.enums.TransactionStatus;
import com.stoloto.vip.domain.enums.TransactionType;
import com.stoloto.vip.repository.TransactionRepository;
import com.stoloto.vip.repository.UserRepository;
import com.stoloto.vip.service.audit.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис управления балансом и транзакциями
 * Реализует паттерн двойной записи для безопасности
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AuditService auditService;

    /**
     * Получение информации о балансе пользователя
     */
    @Transactional(readOnly = true)
    public BalanceDto.BalanceInfo getBalanceInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        return BalanceDto.BalanceInfo.builder()
                .userId(user.getId())
                .mainBalance(user.getMainBalance())
                .bonusBalance(user.getBonusBalance())
                .reservedBalance(user.getReservedBalance() != null ? user.getReservedBalance() : BigDecimal.ZERO)
                .lastUpdatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().toEpochMilli() : 0)
                .build();
    }

    /**
     * Резервирование средств для ставки
     * Используется idempotencyKey для защиты от дублирования
     */
    @Transactional
    public String reserveForBet(Long userId, BigDecimal amount, Long roomId, Long roundId, String idempotencyKey) {
        log.info("Reserving {} for user {} bet in room {}", amount, userId, roomId);
        
        // Проверка на дублирование
        if (idempotencyKey != null) {
            Optional<Transaction> existingTx = transactionRepository.findByIdempotencyKey(idempotencyKey);
            if (existingTx.isPresent()) {
                log.info("Duplicate request detected with key {}, returning existing transaction", idempotencyKey);
                return existingTx.get().getId().toString();
            }
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        if (user.getMainBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance. Required: " + amount + ", Available: " + user.getMainBalance());
        }
        
        // Создание транзакции резервирования
        Transaction transaction = Transaction.builder()
                .userId(userId)
                .amount(amount)
                .type(TransactionType.RESERVE)
                .status(TransactionStatus.PENDING)
                .reason("BET")
                .roomId(roomId)
                .roundId(roundId)
                .idempotencyKey(idempotencyKey)
                .createdAt(Instant.now())
                .build();
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Обновление баланса пользователя
        user.setMainBalance(user.getMainBalance().subtract(amount));
        user.setReservedBalance(user.getReservedBalance() != null 
                ? user.getReservedBalance().add(amount) 
                : amount);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
        
        auditService.logTransactionCreated(savedTransaction.getId(), userId, amount.doubleValue(), "RESERVE_BET");
        
        log.info("Reserved {} for user {}. New balance: {}", amount, userId, user.getMainBalance());
        
        return savedTransaction.getId().toString();
    }

    /**
     * Подтверждение транзакции (списание ставки после начала раунда)
     */
    @Transactional
    public void commitBet(String transactionId, Long roomId, Long roundId) {
        log.info("Committing bet transaction {}", transactionId);
        
        Transaction transaction = transactionRepository.findById(Long.parseLong(transactionId))
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionId));
        
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            log.warn("Transaction {} is not in PENDING status: {}", transactionId, transaction.getStatus());
            return;
        }
        
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setCompletedAt(Instant.now());
        transactionRepository.save(transaction);
        
        // Средства остаются зарезервированными до конца раунда
        
        auditService.logTransactionCompleted(transaction.getId(), transaction.getUserId(), "BET_COMMITTED");
    }

    /**
     * Начисление выигрыша
     */
    @Transactional
    public void creditWin(Long userId, BigDecimal amount, Long roundId) {
        log.info("Crediting win {} to user {} for round {}", amount, userId, roundId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        // Освобождение зарезервированных средств (если были)
        BigDecimal reserved = user.getReservedBalance() != null ? user.getReservedBalance() : BigDecimal.ZERO;
        if (reserved.compareTo(BigDecimal.ZERO) > 0) {
            user.setMainBalance(user.getMainBalance().add(reserved));
            user.setReservedBalance(BigDecimal.ZERO);
            
            // Отметить резерв как использованный
            markReserveAsUsed(userId, roundId);
        }
        
        // Начисление выигрыша
        user.setMainBalance(user.getMainBalance().add(amount));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
        
        // Создание транзакции выигрыша
        Transaction winTransaction = Transaction.builder()
                .userId(userId)
                .amount(amount)
                .type(TransactionType.WIN)
                .status(TransactionStatus.COMPLETED)
                .reason("ROUND_WIN")
                .roundId(roundId)
                .createdAt(Instant.now())
                .completedAt(Instant.now())
                .build();
        
        transactionRepository.save(winTransaction);
        
        auditService.logTransactionCompleted(winTransaction.getId(), userId, "WIN_CREDITED");
        
        log.info("Credited {} to user {}. New balance: {}", amount, userId, user.getMainBalance());
    }

    /**
     * Начисление выигрыша в пользу казино (когда выигрывает бот)
     */
    @Transactional
    public void creditHouse(BigDecimal amount, Long roundId) {
        log.info("Crediting {} to house for round {}", amount, roundId);
        
        // Создание транзакции для учета дохода казино
        Transaction houseTransaction = Transaction.builder()
                .userId(null) // null означает системную транзакцию
                .amount(amount)
                .type(TransactionType.HOUSE_WIN)
                .status(TransactionStatus.COMPLETED)
                .reason("BOT_WIN")
                .roundId(roundId)
                .createdAt(Instant.now())
                .completedAt(Instant.now())
                .build();
        
        transactionRepository.save(houseTransaction);
        
        auditService.logHouseIncome(amount.doubleValue(), roundId);
    }

    /**
     * Возврат средств при откате
     */
    @Transactional
    public void rollbackTransaction(String transactionId, String reason) {
        log.info("Rolling back transaction {} with reason: {}", transactionId, reason);
        
        Transaction transaction = transactionRepository.findById(Long.parseLong(transactionId))
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionId));
        
        if (transaction.getStatus() == TransactionStatus.ROLLED_BACK) {
            log.warn("Transaction {} already rolled back", transactionId);
            return;
        }
        
        User user = userRepository.findById(transaction.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + transaction.getUserId()));
        
        // Возврат средств
        if (transaction.getType() == TransactionType.RESERVE) {
            user.setMainBalance(user.getMainBalance().add(transaction.getAmount()));
            user.setReservedBalance(user.getReservedBalance() != null 
                    ? user.getReservedBalance().subtract(transaction.getAmount()) 
                    : BigDecimal.ZERO);
        }
        
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
        
        // Обновление транзакции
        transaction.setStatus(TransactionStatus.ROLLED_BACK);
        transaction.setReason(reason);
        transaction.setCompletedAt(Instant.now());
        transactionRepository.save(transaction);
        
        auditService.logTransactionRolledBack(transaction.getId(), transaction.getUserId(), reason);
        
        log.info("Rolled back transaction {}. User balance restored.", transactionId);
    }

    /**
     * Покупка буста (резервирование бонусных баллов)
     */
    @Transactional
    public String reserveForBoost(Long userId, BigDecimal cost, Long roomId, Long roundId, String idempotencyKey) {
        log.info("Reserving {} bonus points for user {} boost", cost, userId);
        
        // Проверка на дублирование
        if (idempotencyKey != null) {
            Optional<Transaction> existingTx = transactionRepository.findByIdempotencyKey(idempotencyKey);
            if (existingTx.isPresent()) {
                return existingTx.get().getId().toString();
            }
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        if (user.getBonusBalance().compareTo(cost) < 0) {
            throw new RuntimeException("Insufficient bonus balance. Required: " + cost + ", Available: " + user.getBonusBalance());
        }
        
        // Создание транзакции
        Transaction transaction = Transaction.builder()
                .userId(userId)
                .amount(cost)
                .type(TransactionType.RESERVE)
                .status(TransactionStatus.PENDING)
                .reason("BOOST")
                .roomId(roomId)
                .roundId(roundId)
                .idempotencyKey(idempotencyKey)
                .createdAt(Instant.now())
                .build();
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Обновление баланса
        user.setBonusBalance(user.getBonusBalance().subtract(cost));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
        
        auditService.logTransactionCreated(savedTransaction.getId(), userId, cost.doubleValue(), "RESERVE_BOOST");
        
        return savedTransaction.getId().toString();
    }

    /**
     * Подтверждение покупки буста (списание бонусов)
     */
    @Transactional
    public void commitBoostPurchase(String transactionId) {
        log.info("Committing boost purchase transaction {}", transactionId);
        
        Transaction transaction = transactionRepository.findById(Long.parseLong(transactionId))
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionId));
        
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setType(TransactionType.BOOST_PURCHASE);
        transaction.setCompletedAt(Instant.now());
        transactionRepository.save(transaction);
        
        auditService.logTransactionCompleted(transaction.getId(), transaction.getUserId(), "BOOST_PURCHASED");
    }

    /**
     * Отметка зарезервированных средств как использованных
     */
    private void markReserveAsUsed(Long userId, Long roundId) {
        // Найти все pending резервы для этого пользователя и раунда
        // и пометить их как использованные
        // Это упрощенная реализация
    }
}
