package com.stoloto.vip.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Ставка игрока в раунде
 */
@Entity
@Table(name = "bets", indexes = {
    @Index(name = "idx_bets_round", columnList = "round_id"),
    @Index(name = "idx_bets_user", columnList = "user_id"),
    @Index(name = "idx_bets_winner", columnList = "is_winner")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false, foreignKey = @ForeignKey(name = "fk_bets_round"))
    private Round round;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_bets_user"))
    private User user;

    /**
     * Сумма ставки в минимальных единицах
     */
    @Column(nullable = false)
    private Long amount;

    /**
     * Использован ли буст
     */
    @Column(name = "has_boost", nullable = false)
    @Builder.Default
    private Boolean hasBoost = false;

    @Column(name = "boost_config_id")
    private Long boostConfigId;

    /**
     * Вес ставки для RNG (с учетом буста)
     */
    @Column(name = "win_weight")
    private Double winWeight;

    /**
     * Является ли ставка выигрышной
     */
    @Column(name = "is_winner", nullable = false)
    @Builder.Default
    private Boolean isWinner = false;

    /**
     * Сумма выигрыша (если победил)
     */
    @Column(name = "payout_amount")
    @Builder.Default
    private Long payoutAmount = 0L;

    @Column(length = 50)
    @Builder.Default
    private String status = "PENDING";

    @CreationTimestamp
    @Column(name = "placed_at", nullable = false, updatable = false)
    private Instant placedAt;

    @Column(name = "settled_at")
    private Instant settledAt;

    /**
     * Подтверждена ли ставка
     */
    public boolean isConfirmed() {
        return "CONFIRMED".equals(status) || "SETTLED".equals(status);
    }

    /**
     * Завершена ли ставка
     */
    public boolean isSettled() {
        return "SETTLED".equals(status) || "CANCELLED".equals(status);
    }
}
