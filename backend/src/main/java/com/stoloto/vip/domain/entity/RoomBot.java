package com.stoloto.vip.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Присутствие бота в комнате
 */
@Entity
@Table(name = "room_bots", indexes = {
    @Index(name = "idx_room_bots_room", columnList = "room_id"),
    @Index(name = "idx_room_bots_profile", columnList = "bot_profile_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomBot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false, foreignKey = @ForeignKey(name = "fk_room_bots_room"))
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bot_profile_id", nullable = false, foreignKey = @ForeignKey(name = "fk_room_bots_profile"))
    private BotProfile botProfile;

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;

    @Column(name = "left_at")
    private Instant leftAt;

    /**
     * Текущая сумма ставки бота
     */
    @Column(name = "current_bet_amount")
    @Builder.Default
    private Long currentBetAmount = 0L;

    /**
     * Использует ли бот буст (боты не используют бусты по ТЗ)
     */
    @Column(name = "has_active_boost", nullable = false)
    @Builder.Default
    private Boolean hasActiveBoost = false;

    /**
     * Активен ли бот в комнате (не вышел)
     */
    public boolean isActive() {
        return leftAt == null;
    }

    /**
     * Покинуть комнату
     */
    public void leave() {
        this.leftAt = Instant.now();
    }
}
