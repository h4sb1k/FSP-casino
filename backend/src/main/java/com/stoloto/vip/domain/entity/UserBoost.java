package com.stoloto.vip.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * История использования бустов игроками
 */
@Entity
@Table(name = "user_boosts", indexes = {
    @Index(name = "idx_user_boosts_user", columnList = "user_id"),
    @Index(name = "idx_user_boosts_round", columnList = "round_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_boosts_user_round", columnNames = {"user_id", "round_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBoost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_boosts_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boost_config_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_boosts_config"))
    private BoostConfig boostConfig;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_boosts_round"))
    private Round round;

    /**
     * Потрачено бонусов
     */
    @Column(name = "bonus_spent", nullable = false)
    private Long bonusSpent;

    @CreationTimestamp
    @Column(name = "applied_at", nullable = false, updatable = false)
    private Instant appliedAt;
}
