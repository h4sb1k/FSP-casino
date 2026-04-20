package com.stoloto.vip.service.audit;

import com.stoloto.vip.domain.entity.AuditLog;
import com.stoloto.vip.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

/**
 * Сервис аудита и логирования всех значимых действий в системе
 * Все записи сохраняются в immutable таблицу audit_logs
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Логирование создания комнаты
     */
    @Async
    @Transactional
    public void logRoomCreated(Long roomId, Long userId, String roomType) {
        AuditLog log = createAuditLog(
                "ROOM_CREATED",
                "USER",
                userId,
                Map.of("roomId", roomId, "roomType", roomType),
                null
        );
        auditLogRepository.save(log);
        log.debug("Room {} created by user {}", roomId, userId);
    }

    /**
     * Логирование присоединения игрока к комнате
     */
    @Async
    @Transactional
    public void logPlayerJoinedRoom(Long roomId, Long userId, double betAmount) {
        AuditLog log = createAuditLog(
                "PLAYER_JOINED_ROOM",
                "USER",
                userId,
                Map.of("roomId", roomId, "betAmount", betAmount),
                null
        );
        auditLogRepository.save(log);
    }

    /**
     * Логирование активации буста
     */
    @Async
    @Transactional
    public void logBoostActivated(Long roomId, Long userId, Long boostConfigId) {
        AuditLog log = createAuditLog(
                "BOOST_ACTIVATED",
                "USER",
                userId,
                Map.of("roomId", roomId, "boostConfigId", boostConfigId),
                null
        );
        auditLogRepository.save(log);
    }

    /**
     * Логирование начала раунда
     */
    @Async
    @Transactional
    public void logRoundStarted(Long roundId, Long roomId, int playerCount) {
        AuditLog log = createAuditLog(
                "ROUND_STARTED",
                "SYSTEM",
                null,
                Map.of("roundId", roundId, "roomId", roomId, "playerCount", playerCount),
                null
        );
        auditLogRepository.save(log);
    }

    /**
     * Логирование завершения раунда
     */
    @Async
    @Transactional
    public void logRoundCompleted(Long roundId, Long winnerId, double winAmount, String rngSeedProof) {
        AuditLog log = createAuditLog(
                "ROUND_COMPLETED",
                "SYSTEM",
                null,
                Map.of(
                        "roundId", roundId,
                        "winnerId", winnerId,
                        "winAmount", winAmount,
                        "rngSeedProof", rngSeedProof
                ),
                null
        );
        auditLogRepository.save(log);
    }

    /**
     * Логирование создания транзакции
     */
    @Async
    @Transactional
    public void logTransactionCreated(Long transactionId, Long userId, double amount, String type) {
        AuditLog log = createAuditLog(
                "TRANSACTION_CREATED",
                "SYSTEM",
                null,
                Map.of("transactionId", transactionId, "userId", userId, "amount", amount, "type", type),
                null
        );
        auditLogRepository.save(log);
    }

    /**
     * Логирование завершения транзакции
     */
    @Async
    @Transactional
    public void logTransactionCompleted(Long transactionId, Long userId, String status) {
        AuditLog log = createAuditLog(
                "TRANSACTION_COMPLETED",
                "SYSTEM",
                null,
                Map.of("transactionId", transactionId, "userId", userId, "status", status),
                null
        );
        auditLogRepository.save(log);
    }

    /**
     * Логирование отката транзакции
     */
    @Async
    @Transactional
    public void logTransactionRolledBack(Long transactionId, Long userId, String reason) {
        AuditLog log = createAuditLog(
                "TRANSACTION_ROLLED_BACK",
                "SYSTEM",
                null,
                Map.of("transactionId", transactionId, "userId", userId, "reason", reason),
                null
        );
        auditLogRepository.save(log);
    }

    /**
     * Логирование дохода казино (выигрыш бота)
     */
    @Async
    @Transactional
    public void logHouseIncome(double amount, Long roundId) {
        AuditLog log = createAuditLog(
                "HOUSE_INCOME",
                "SYSTEM",
                null,
                Map.of("amount", amount, "roundId", roundId, "source", "BOT_WIN"),
                null
        );
        auditLogRepository.save(log);
    }

    /**
     * Логирование действия администратора
     */
    @Async
    @Transactional
    public void logAdminAction(Long adminId, String action, Map<String, Object> details) {
        AuditLog log = createAuditLog(
                "ADMIN_ACTION",
                "ADMIN",
                adminId,
                details,
                action
        );
        auditLogRepository.save(log);
    }

    /**
     * Логирование входа пользователя
     */
    @Async
    @Transactional
    public void logUserLogin(Long userId, String ipAddress, String userAgent) {
        AuditLog log = createAuditLog(
                "USER_LOGIN",
                "USER",
                userId,
                Map.of("ipAddress", ipAddress, "userAgent", userAgent),
                null
        );
        auditLogRepository.save(log);
    }

    /**
     * Логирование выхода пользователя
     */
    @Async
    @Transactional
    public void logUserLogout(Long userId) {
        AuditLog log = createAuditLog(
                "USER_LOGOUT",
                "USER",
                userId,
                Map.of(),
                null
        );
        auditLogRepository.save(log);
    }

    /**
     * Логирование ошибки безопасности
     */
    @Async
    @Transactional
    public void logSecurityEvent(String eventType, Long userId, String details) {
        AuditLog log = createAuditLog(
                "SECURITY_EVENT",
                "SYSTEM",
                userId,
                Map.of("eventType", eventType, "details", details),
                eventType
        );
        auditLogRepository.save(log);
        log.warn("Security event: {} for user {}", eventType, userId);
    }

    /**
     * Создание базовой записи аудита
     */
    private AuditLog createAuditLog(String type, String actorType, Long actorId, 
                                    Map<String, Object> contextData, String reason) {
        return AuditLog.builder()
                .timestamp(Instant.now())
                .type(type)
                .actorType(actorType)
                .actorId(actorId)
                .contextData(convertToJson(contextData))
                .payload(reason)
                .build();
    }

    /**
     * Преобразование Map в JSON строку
     * В продакшене использовать Jackson ObjectMapper
     */
    private String convertToJson(Map<String, Object> data) {
        if (data == null || data.isEmpty()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":");
            if (entry.getValue() instanceof String) {
                sb.append("\"").append(entry.getValue()).append("\"");
            } else {
                sb.append(entry.getValue());
            }
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
}
