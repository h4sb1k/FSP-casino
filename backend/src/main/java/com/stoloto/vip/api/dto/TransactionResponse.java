package com.stoloto.vip.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для отображения транзакции.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private Long userId;
    private String type; // BET, WIN, BOOST, DEPOSIT, WITHDRAW, ROLLBACK
    private Long amount;
    private Long balanceAfter;
    private String status; // PENDING, COMPLETED, ROLLED_BACK
    private String context; // JSON с деталями (roomId, roundId)
    private java.time.Instant createdAt;
}
