package com.stoloto.vip.core.service;

import com.stoloto.vip.core.entity.User;
import com.stoloto.vip.core.entity.Transaction;
import com.stoloto.vip.core.enums.TransactionType;
import com.stoloto.vip.core.repository.TransactionRepository;
import com.stoloto.vip.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public User deposit(Long userId, BigDecimal amount, String description) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        BigDecimal balanceBefore = user.getBalance();
        user.setBalance(balanceBefore.add(amount));

        Transaction transaction = Transaction.builder()
                .user(user)
                .type(TransactionType.DEPOSIT)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(user.getBalance())
                .description(description)
                .build();

        transactionRepository.save(transaction);
        userRepository.save(user);

        log.info("Deposit: user={}, amount={}, newBalance={}", userId, amount, user.getBalance());
        return user;
    }

    @Transactional
    public User withdraw(Long userId, BigDecimal amount, String description) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (user.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        BigDecimal balanceBefore = user.getBalance();
        user.setBalance(balanceBefore.subtract(amount));

        Transaction transaction = Transaction.builder()
                .user(user)
                .type(TransactionType.WITHDRAWAL)
                .amount(amount.negate())
                .balanceBefore(balanceBefore)
                .balanceAfter(user.getBalance())
                .description(description)
                .build();

        transactionRepository.save(transaction);
        userRepository.save(user);

        log.info("Withdrawal: user={}, amount={}, newBalance={}", userId, amount, user.getBalance());
        return user;
    }

    @Transactional
    public User placeBet(Long userId, BigDecimal betAmount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (user.getBalance().compareTo(betAmount) < 0) {
            throw new IllegalArgumentException("Insufficient balance for bet");
        }

        BigDecimal balanceBefore = user.getBalance();
        user.setBalance(balanceBefore.subtract(betAmount));

        Transaction transaction = Transaction.builder()
                .user(user)
                .type(TransactionType.BET_PLACE)
                .amount(betAmount.negate())
                .balanceBefore(balanceBefore)
                .balanceAfter(user.getBalance())
                .description("Bet placed")
                .build();

        transactionRepository.save(transaction);
        userRepository.save(user);

        return user;
    }

    @Transactional
    public User addWin(Long userId, BigDecimal winAmount, String referenceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        BigDecimal balanceBefore = user.getBalance();
        user.setBalance(balanceBefore.add(winAmount));

        Transaction transaction = Transaction.builder()
                .user(user)
                .type(TransactionType.BET_WIN)
                .amount(winAmount)
                .balanceBefore(balanceBefore)
                .balanceAfter(user.getBalance())
                .description("Win from round")
                .referenceId(referenceId)
                .build();

        transactionRepository.save(transaction);
        userRepository.save(user);

        log.info("Win added: user={}, amount={}, newBalance={}", userId, winAmount, user.getBalance());
        return user;
    }

    @Transactional(readOnly = true)
    public User getUserWithBalance(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }
}
