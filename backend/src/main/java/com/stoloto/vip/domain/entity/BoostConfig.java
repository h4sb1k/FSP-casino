package com.stoloto.vip.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * Конфигурация буста (увеличение шанса победы)
 */
@Entity
@Table(name = "boost_configs", indexes = {
    @Index(name = "idx_boost_configs_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoostConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    /**
     * Стоимость в бонусных баллах
     */
    @Column(name = "cost_in_bonus", nullable = false)
    private Long costInBonus;

    /**
     * Процент увеличения вероятности победы (например, 5.0 = +5%)
     */
    @Column(name = "win_probability_bonus_percent", nullable = false)
    private Double winProbabilityBonusPercent;

    /**
     * Максимальное использование за раунд (обычно 1)
     */
    @Column(name = "max_usage_per_round")
    @Builder.Default
    private Integer maxUsagePerRound = 1;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Список типов комнат через запятую (BRONZE,GOLD,DIAMOND) или NULL для всех
     */
    @Column(name = "applicable_room_types", length = 255)
    private String applicableRoomTypes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * Применим ли буст к указанному типу комнаты
     */
    public boolean isApplicableToRoomType(String roomType) {
        if (applicableRoomTypes == null || applicableRoomTypes.isBlank()) {
            return true; // Применим ко всем типам
        }
        return applicableRoomTypes.contains(roomType);
    }

    /**
     * Активен ли буст и доступен для использования
     */
    public boolean isAvailable() {
        return isActive != null && isActive;
    }
}
