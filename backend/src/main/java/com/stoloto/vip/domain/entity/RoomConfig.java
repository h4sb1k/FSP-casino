package com.stoloto.vip.domain.entity;

import com.stoloto.vip.domain.enums.RoomType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * Конфигурация комнаты (шаблон настроек)
 */
@Entity
@Table(name = "room_configs", indexes = {
    @Index(name = "idx_room_configs_type", columnList = "type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private RoomType type;

    /**
     * Минимальная ставка в минимальных единицах
     */
    @Column(name = "min_bet", nullable = false)
    private Long minBet;

    /**
     * Максимальная ставка в минимальных единицах
     */
    @Column(name = "max_bet", nullable = false)
    private Long maxBet;

    /**
     * Максимальное количество игроков в комнате
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer capacity = 8;

    /**
     * Порог заполнения ботами (если игроков меньше этого числа)
     */
    @Column(name = "bot_fill_threshold", columnDefinition = "INTEGER DEFAULT 2")
    @Builder.Default
    private Integer botFillThreshold = 2;

    /**
     * Длительность раунда в секундах
     */
    @Column(name = "round_duration_seconds", nullable = false)
    @Builder.Default
    private Integer roundDurationSeconds = 60;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * Проверка допустимости ставки для этой комнаты
     */
    public boolean isValidBet(Long amount) {
        return amount != null && amount >= minBet && amount <= maxBet;
    }
}
