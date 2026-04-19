package com.stoloto.vip.core.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "boost_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoostConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer level; // 1, 2, 3

    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal cost; // Стоимость буста

    @Column(precision = 5, scale = 4, nullable = false)
    private BigDecimal probabilityBonus; // Добавка к вероятности (0.05 = 5%)

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column
    private String description;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (active == null) active = true;
    }
}
