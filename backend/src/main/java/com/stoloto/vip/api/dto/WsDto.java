package com.stoloto.vip.api.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO для WebSocket сообщений
 */
public class WsDto {
    
    /**
     * Базовое сообщение WebSocket
     */
    @Data
    @Builder
    public static class WsMessage<T> {
        private String type; // JOIN_ROOM, PLACE_BET, ROUND_START, etc.
        private Long roomId;
        private Long roundId;
        private T payload;
        private long timestamp;
    }

    /**
     * Событие: игрок присоединился к комнате
     */
    @Data
    @Builder
    public static class PlayerJoinedEvent {
        private Long userId;
        private String username;
        private boolean isBot;
        private int currentPlayers;
        private int maxPlayers;
    }

    /**
     * Событие: игрок покинул комнату
     */
    @Data
    @Builder
    public static class PlayerLeftEvent {
        private Long userId;
        private String username;
        private int remainingPlayers;
    }

    /**
     * Событие: ставка сделана
     */
    @Data
    @Builder
    public static class BetPlacedEvent {
        private Long userId;
        private String username;
        private BigDecimal amount;
        private boolean hasBoost;
    }

    /**
     * Событие: буст активирован
     */
    @Data
    @Builder
    public static class BoostActivatedEvent {
        private Long userId;
        private String username;
        private Long boostConfigId;
        private int bonusPercent;
    }

    /**
     * Событие: раунд начинается
     */
    @Data
    @Builder
    public static class RoundStartingEvent {
        private Long roundId;
        private Instant startTime;
        private long durationSeconds;
        private int playerCount;
    }

    /**
     * Событие: раунд заканчивается (предупреждение)
     */
    @Data
    @Builder
    public static class RoundEndingSoonEvent {
        private Long roundId;
        private long secondsLeft;
    }

    /**
     * Событие: результаты раунда
     */
    @Data
    @Builder
    public static class RoundResultEvent {
        private Long roundId;
        private Long winnerId;
        private String winnerName;
        private boolean winnerIsBot;
        private BigDecimal winAmount;
        private String rngSeedProof;
    }

    /**
     * Событие: ошибка
     */
    @Data
    @Builder
    public static class ErrorEvent {
        private String code; // INSUFFICIENT_BALANCE, ROOM_FULL, INVALID_BET, etc.
        private String message;
        private Long roomId;
    }

    /**
     * Событие: обновление баланса
     */
    @Data
    @Builder
    public static class BalanceUpdateEvent {
        private Long userId;
        private BigDecimal mainBalance;
        private BigDecimal bonusBalance;
        private BigDecimal reservedBalance;
    }

    /**
     * Событие: уведомление (для push)
     */
    @Data
    @Builder
    public static class NotificationEvent {
        private String type; // WIN, ROUND_START, NEW_ROUND
        private String title;
        private String message;
        private Long roomId;
        private Long roundId;
    }
}
