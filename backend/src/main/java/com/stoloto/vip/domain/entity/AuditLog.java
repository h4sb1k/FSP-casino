package com.stoloto.vip.domain.entity;

import com.stoloto.vip.domain.enums.AuditActionType;
import com.stoloto.vip.domain.enums.AuditActorType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Запись аудита всех действий в системе
 */
@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_timestamp", columnList = "timestamp"),
    @Index(name = "idx_audit_action_type", columnList = "action_type"),
    @Index(name = "idx_audit_actor", columnList = {"actor_type", "actor_id"}),
    @Index(name = "idx_audit_room", columnList = "room_id"),
    @Index(name = "idx_audit_round", columnList = "round_id"),
    @Index(name = "idx_audit_critical", columnList = "is_critical")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 50)
    private AuditActionType actionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "actor_type", nullable = false, length = 50)
    private AuditActorType actorType;

    /**
     * ID актора (пользователя, бота) или NULL для SYSTEM
     */
    @Column(name = "actor_id")
    private Long actorId;

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "round_id")
    private Long roundId;

    /**
     * JSON с контекстом: {roomId, roundId, prevState, newState}
     */
    @Column(name = "context_data", columnDefinition = "TEXT")
    private String contextData;

    /**
     * JSON с деталями действия: суммы, семена RNG, результаты
     */
    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /**
     * SHA256 хэш записи для проверки целостности
     */
    @Column(length = 64)
    private String signature;

    /**
     * Флаг критичности (требует внимания админа)
     */
    @Column(name = "is_critical")
    @Builder.Default
    private Boolean isCritical = false;

    /**
     * Является ли запись критической
     */
    public boolean isCriticalAlert() {
        return isCritical != null && isCritical;
    }
}
