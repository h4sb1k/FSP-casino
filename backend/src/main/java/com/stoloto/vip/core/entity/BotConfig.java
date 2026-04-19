package com.stoloto.vip.core.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "bot_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BotConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Double minBetPercentage = 0.01; // Мин. ставка от баланса

    @Column(nullable = false)
    @Builder.Default
    private Double maxBetPercentage = 0.1; // Макс. ставка от баланса

    @Column(nullable = false)
    @Builder.Default
    private Double buyBoostProbability = 0.3; // Вероятность покупки буста

    @Column(nullable = false)
    @Builder.Default
    private Integer maxBoostLevel = 2; // Максимальный уровень буста

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column
    private String behaviorPattern; // JSON с паттерном поведения

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (minBetPercentage == null) minBetPercentage = 0.01;
        if (maxBetPercentage == null) maxBetPercentage = 0.1;
        if (buyBoostProbability == null) buyBoostProbability = 0.3;
        if (maxBoostLevel == null) maxBoostLevel = 2;
        if (active == null) active = true;
    }
}
