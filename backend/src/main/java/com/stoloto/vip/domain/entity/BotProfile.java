package com.stoloto.vip.domain.entity;

import com.stoloto.vip.domain.enums.BotStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * Профиль бота для заполнения комнат
 */
@Entity
@Table(name = "bot_profiles", indexes = {
    @Index(name = "idx_bot_profiles_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BotProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private BotStatus status = BotStatus.ACTIVE;

    /**
     * Стратегия поведения: RANDOM, CONSERVATIVE, AGGRESSIVE, BALANCED
     */
    @Column(name = "strategy_type", length = 50)
    @Builder.Default
    private String strategyType = "RANDOM";

    /**
     * Множитель минимальной ставки комнаты
     */
    @Column(name = "min_bet_multiplier")
    @Builder.Default
    private Double minBetMultiplier = 1.0;

    /**
     * Множитель максимальной ставки комнаты
     */
    @Column(name = "max_bet_multiplier")
    @Builder.Default
    private Double maxBetMultiplier = 5.0;

    /**
     * Вероятность присоединения к комнате при возможности (0.0 - 1.0)
     */
    @Column(name = "join_probability")
    @Builder.Default
    private Double joinProbability = 0.8;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * Активен ли бот и может ли играть
     */
    public boolean isAvailable() {
        return status == BotStatus.ACTIVE || status == BotStatus.IN_GAME;
    }
}
