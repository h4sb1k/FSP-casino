package com.stoloto.vip.domain.entity;

import com.stoloto.vip.domain.enums.RoomStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Активная игровая комната (инстанс)
 */
@Entity
@Table(name = "rooms", indexes = {
    @Index(name = "idx_rooms_status", columnList = "status"),
    @Index(name = "idx_rooms_config", columnList = "config_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "config_id", nullable = false, foreignKey = @ForeignKey(name = "fk_rooms_config"))
    private RoomConfig config;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private RoomStatus status = RoomStatus.WAITING;

    @Column(name = "current_round_id")
    private Long currentRoundId;

    /**
     * Текущее количество игроков в комнате
     */
    @Column(name = "player_count", nullable = false)
    @Builder.Default
    private Integer playerCount = 0;

    /**
     * Общий призовой фонд текущего раунда
     */
    @Column(name = "total_prize_pool", columnDefinition = "BIGINT DEFAULT 0")
    @Builder.Default
    private Long totalPrizePool = 0L;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "closed_at")
    private Instant closedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Есть ли свободные места в комнате
     */
    public boolean hasFreeSlots() {
        return playerCount < config.getCapacity();
    }

    /**
     * Нужно ли заполнить комнату ботами
     */
    public boolean needsBotFill() {
        return playerCount < config.getBotFillThreshold();
    }

    /**
     * Может ли комната начать раунд
     */
    public boolean canStartRound() {
        return playerCount >= config.getBotFillThreshold() && 
               (status == RoomStatus.WAITING || status == RoomStatus.ACTIVE);
    }
}
