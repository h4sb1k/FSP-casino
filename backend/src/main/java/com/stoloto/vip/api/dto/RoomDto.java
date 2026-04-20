package com.stoloto.vip.api.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * DTO классы для работы с комнатами и игровыми сессиями
 */
public class RoomDto {
    
    /**
     * Основная информация о комнате
     */
    @Data
    @Builder
    public static class RoomInfo {
        private Long id;
        private String name;
        private RoomType type; // BRONZE, GOLD, DIAMOND
        private BigDecimal minBet;
        private BigDecimal maxBet;
        private int capacity;
        private int currentPlayers;
        private RoomStatus status; // WAITING, STARTING, IN_GAME
        private Instant startTime;
        private long timeLeftSeconds;
        private boolean hasBoostOption;
    }

    /**
     * Информация об игроке в комнате
     */
    @Data
    @Builder
    public static class PlayerInRoom {
        private Long userId;
        private String username;
        private BigDecimal betAmount;
        private boolean hasBoost;
        private boolean isBot;
        private Instant joinedAt;
    }

    /**
     * Полное состояние комнаты
     */
    @Data
    @Builder
    public static class RoomState {
        private RoomInfo room;
        private List<PlayerInRoom> players;
        private Long roundId;
        private String message;
    }

    /**
     * Запрос на создание комнаты
     */
    @Data
    @Builder
    public static class CreateRoomRequest {
        private RoomType type;
    }

    /**
     * Запрос на присоединение к комнате
     */
    @Data
    @Builder
    public static class JoinRoomRequest {
        private Long roomId;
        private BigDecimal betAmount;
    }

    /**
     * Запрос на покупку буста
     */
    @Data
    @Builder
    public static class BoostRequest {
        private Long roomId;
        private Long boostConfigId;
    }

    /**
     * Результат раунда
     */
    @Data
    @Builder
    public static class RoomResult {
        private Long roundId;
        private Long winnerId;
        private String winnerName;
        private boolean winnerIsBot;
        private BigDecimal winAmount;
        private List<PlayerResult> allResults;
        private String rngSeedProof;
    }

    /**
     * Результат игрока в раунде
     */
    @Data
    @Builder
    public static class PlayerResult {
        private Long userId;
        private String username;
        private boolean isBot;
        private BigDecimal bet;
        private boolean hasBoost;
        private BigDecimal payout;
    }
}
