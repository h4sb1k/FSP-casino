package com.stoloto.vip.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Участник комнаты (игрок или бот)
 */
@Entity
@Table(name = "room_participants", indexes = {
    @Index(name = "idx_participants_room", columnList = "room_id"),
    @Index(name = "idx_participants_user", columnList = "user_id"),
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_participants_room_user", columnNames = {"room_id", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false, foreignKey = @ForeignKey(name = "fk_participants_room"))
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_participants_user"))
    private User user;

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;

    @Column(name = "left_at")
    private Instant leftAt;

    /**
     * Является ли участник ботом
     */
    @Column(name = "is_bot", nullable = false)
    @Builder.Default
    private Boolean isBot = false;

    /**
     * Активен ли участник в комнате (не вышел)
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
