package com.stoloto.vip.core.entity;

import com.stoloto.vip.core.enums.RoomStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer minPlayers = 2;

    @Column(nullable = false)
    private Integer maxPlayers = 10;

    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal minBet;

    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal maxBet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RoomStatus status = RoomStatus.WAITING;

    @Column(nullable = false)
    @Builder.Default
    private Boolean autoStart = true;

    @Column(nullable = false)
    @Builder.Default
    private Integer roundDurationSeconds = 60;

    @Column(nullable = false)
    @Builder.Default
    private Integer bettingDurationSeconds = 30;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RoomBot> botConfigs = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Round> rounds = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (minPlayers == null) minPlayers = 2;
        if (maxPlayers == null) maxPlayers = 10;
        if (status == null) status = RoomStatus.WAITING;
        if (autoStart == null) autoStart = true;
        if (roundDurationSeconds == null) roundDurationSeconds = 60;
        if (bettingDurationSeconds == null) bettingDurationSeconds = 30;
    }
}
