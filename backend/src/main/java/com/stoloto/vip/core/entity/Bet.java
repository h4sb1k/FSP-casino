package com.stoloto.vip.core.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bets")
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
    @JoinColumn(name = "round_id", nullable = false)
    private Round round;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal amount;

    @Column(precision = 19, scale = 4)
    private BigDecimal winAmount;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isBot = false;

    @Column(nullable = false)
    @Builder.Default
    private Integer boostLevel = 0; // 0, 1, 2, 3

    @Column(precision = 5, scale = 4)
    private BigDecimal winProbability; // Базовая вероятность + буст

    @Column
    private LocalDateTime settledAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (isBot == null) isBot = false;
        if (boostLevel == null) boostLevel = 0;
    }
}
