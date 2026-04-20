package com.stoloto.vip.domain.entity;

import com.stoloto.vip.domain.enums.RoundStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Игровой раунд с поддержкой Provably Fair RNG
 */
@Entity
@Table(name = "rounds", indexes = {
    @Index(name = "idx_rounds_room", columnList = "room_id"),
    @Index(name = "idx_rounds_status", columnList = "status")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_rounds_room_number", columnNames = {"room_id", "round_number"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false, foreignKey = @ForeignKey(name = "fk_rounds_room"))
    private Room room;

    @Column(name = "round_number", nullable = false)
    private Integer roundNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private RoundStatus status = RoundStatus.WAITING_FOR_PLAYERS;

    /**
     * Хэш серверного seed (публикуется до начала раунда)
     */
    @Column(name = "server_seed_hash", length = 64)
    private String serverSeedHash;

    /**
     * Серверный seed (раскрывается после завершения раунда)
     */
    @Column(name = "server_seed", length = 64)
    private String serverSeed;

    /**
     * Клиентский seed (опционально от игроков)
     */
    @Column(name = "client_seed", length = 64)
    private String clientSeed;

    /**
     * Счетчик для уникальности хэша
     */
    @Column(nullable = false)
    @Builder.Default
    private Long nonce = 0L;

    /**
     * Общая сумма ставок в раунде
     */
    @Column(name = "total_bets", nullable = false)
    @Builder.Default
    private Long totalBets = 0L;

    /**
     * Общее количество участников
     */
    @Column(name = "total_participants", nullable = false)
    @Builder.Default
    private Integer totalParticipants = 0;

    @Column(name = "betting_started_at")
    private Instant bettingStartedAt;

    @Column(name = "betting_ends_at")
    private Instant bettingEndsAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Можно ли еще делать ставки
     */
    public boolean isBettingOpen() {
        return status == RoundStatus.BETTING_OPEN;
    }

    /**
     * Завершен ли раунд
     */
    public boolean isCompleted() {
        return status == RoundStatus.COMPLETED || status == RoundStatus.CANCELLED;
    }
}
