package com.stoloto.vip.core.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "room_bots")
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
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bot_config_id", nullable = false)
    private BotConfig botConfig;

    @Column(nullable = false)
    @Builder.Default
    private Integer minInstances = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer maxInstances = 5;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (minInstances == null) minInstances = 0;
        if (maxInstances == null) maxInstances = 5;
        if (active == null) active = true;
    }
}
