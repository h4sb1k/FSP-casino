package com.stoloto.vip.domain.entity;

import com.stoloto.vip.domain.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Финансовая транзакция (Double Entry Bookkeeping)
 */
@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_transactions_user", columnList = "user_id"),
    @Index(name = "idx_transactions_type", columnList = "type"),
    @Index(name = "idx_transactions_reference", columnList = {"reference_type", "reference_id"}),
    @Index(name = "idx_transactions_idempotency", columnList = "idempotency_key")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transactions_user"))
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TransactionType type;

    /**
     * Сумма транзакции (всегда положительная, направление определяется типом)
     */
    @Column(nullable = false)
    private Long amount;

    @Column(name = "balance_before", nullable = false)
    private Long balanceBefore;

    @Column(name = "balance_after", nullable = false)
    private Long balanceAfter;

    @Column(name = "bonus_balance_before")
    @Builder.Default
    private Long bonusBalanceBefore = 0L;

    @Column(name = "bonus_balance_after")
    @Builder.Default
    private Long bonusBalanceAfter = 0L;

    /**
     * Тип связанной сущности (BET, ROUND, BOOST и т.д.)
     */
    @Column(name = "reference_type", length = 50)
    private String referenceType;

    /**
     * ID связанной сущности
     */
    @Column(name = "reference_id")
    private Long referenceId;

    /**
     * Ключ идемпотентности для защиты от дублей
     */
    @Column(name = "idempotency_key", length = 100)
    private String idempotencyKey;

    @Column(length = 50)
    @Builder.Default
    private String status = "COMPLETED";

    @Column(length = 500)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Успешна ли транзакция
     */
    public boolean isSuccessful() {
        return "COMPLETED".equals(status);
    }

    /**
     * Отменена/возвращена ли транзакция
     */
    public boolean isRolledBack() {
        return "ROLLED_BACK".equals(status);
    }
}
