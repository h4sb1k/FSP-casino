package com.stoloto.vip.api.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO для операций с балансом и транзакциями
 */
public class BalanceDto {
    
    /**
     * Информация о балансе пользователя
     */
    @Data
    @Builder
    public static class BalanceInfo {
        private Long userId;
        private BigDecimal mainBalance;
        private BigDecimal bonusBalance;
        private BigDecimal reservedBalance; // Зарезервировано на текущую игру
        private long lastUpdatedAt;
    }

    /**
     * Запрос на резервирование средств
     */
    @Data
    @Builder
    public static class ReserveRequest {
        private Long userId;
        private BigDecimal amount;
        private String reason; // "BET", "BOOST"
        private String idempotencyKey; // Для защиты от дублирования
        private Long roomId;
        private Long roundId;
    }

    /**
     * Запрос на подтверждение транзакции
     */
    @Data
    @Builder
    public static class CommitRequest {
        private Long userId;
        private String transactionId;
        private BigDecimal amount;
        private String type; // "WIN", "LOSS", "REFUND"
    }

    /**
     * Запрос на откат транзакции
     */
    @Data
    @Builder
    public static class RollbackRequest {
        private Long userId;
        private String transactionId;
        private String reason; // "TIMEOUT", "ERROR", "CANCEL"
    }

    /**
     * Информация о транзакции
     */
    @Data
    @Builder
    public static class TransactionInfo {
        private Long id;
        private Long userId;
        private BigDecimal amount;
        private String type; // BET, WIN, BOOST_PURCHASE, RESERVE, RELEASE, REFUND
        private String status; // PENDING, COMPLETED, ROLLED_BACK
        private String reason;
        private Long roomId;
        private Long roundId;
        private String idempotencyKey;
        private long createdAt;
    }
}
