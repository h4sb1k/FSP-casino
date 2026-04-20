package com.stoloto.vip.realtime.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Публикация событий в Redis Pub/Sub для real-time уведомлений
 * Используется для масштабирования и связи между инстансами приложения
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisPubSubPublisher {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    // Каналы Redis
    private static final String CHANNEL_ROOM_PREFIX = "rooms:";
    private static final String CHANNEL_USER_PREFIX = "users:";
    private static final String CHANNEL_ADMIN = "admin:alerts";

    /**
     * Публикация события о начале раунда
     */
    public void publishRoundStart(Long roomId, Long roundId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "ROUND_START");
        payload.put("roomId", roomId);
        payload.put("roundId", roundId);
        payload.put("timestamp", System.currentTimeMillis());
        
        publish(CHANNEL_ROOM_PREFIX + roomId + ":events", payload);
        log.debug("Published ROUND_START for room {}, round {}", roomId, roundId);
    }

    /**
     * Публикация результатов раунда
     */
    public void publishRoundResult(Long roomId, Long roundId, Object result) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "ROUND_RESULT");
        payload.put("roomId", roomId);
        payload.put("roundId", roundId);
        payload.put("result", result);
        payload.put("timestamp", System.currentTimeMillis());
        
        publish(CHANNEL_ROOM_PREFIX + roomId + ":events", payload);
        log.debug("Published ROUND_RESULT for room {}, round {}", roomId, roundId);
    }

    /**
     * Публикация события о присоединении игрока
     */
    public void publishPlayerJoined(Long roomId, Long userId, String username, boolean isBot) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "PLAYER_JOINED");
        payload.put("roomId", roomId);
        payload.put("userId", userId);
        payload.put("username", username);
        payload.put("isBot", isBot);
        payload.put("timestamp", System.currentTimeMillis());
        
        publish(CHANNEL_ROOM_PREFIX + roomId + ":events", payload);
    }

    /**
     * Публикация события об изменении баланса пользователя
     */
    public void publishBalanceUpdate(Long userId, double mainBalance, double bonusBalance, double reservedBalance) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "BALANCE_UPDATE");
        payload.put("userId", userId);
        payload.put("mainBalance", mainBalance);
        payload.put("bonusBalance", bonusBalance);
        payload.put("reservedBalance", reservedBalance);
        payload.put("timestamp", System.currentTimeMillis());
        
        publish(CHANNEL_USER_PREFIX + userId + ":balance", payload);
    }

    /**
     * Публикация уведомления для пользователя
     */
    public void publishNotification(Long userId, String type, String title, String message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", type);
        payload.put("title", title);
        payload.put("message", message);
        payload.put("timestamp", System.currentTimeMillis());
        
        publish(CHANNEL_USER_PREFIX + userId + ":notifications", payload);
    }

    /**
     * Публикация алерта для администраторов
     */
    public void publishAdminAlert(String alertType, String message, Map<String, Object> details) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("alertType", alertType);
        payload.put("message", message);
        payload.put("details", details);
        payload.put("timestamp", System.currentTimeMillis());
        
        publish(CHANNEL_ADMIN, payload);
        log.warn("Admin alert published: {} - {}", alertType, message);
    }

    /**
     * Публикация события об ошибке в комнате
     */
    public void publishRoomError(Long roomId, String errorCode, String errorMessage) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "ERROR");
        payload.put("roomId", roomId);
        payload.put("errorCode", errorCode);
        payload.put("errorMessage", errorMessage);
        payload.put("timestamp", System.currentTimeMillis());
        
        publish(CHANNEL_ROOM_PREFIX + roomId + ":events", payload);
    }

    /**
     * Базовый метод публикации
     */
    private void publish(String channel, Map<String, Object> payload) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(payload);
            redisTemplate.convertAndSend(channel, jsonMessage);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize message for channel {}", channel, e);
        }
    }

    /**
     * Публикация обновления состояния комнаты
     */
    public void publishRoomState(Long roomId, Map<String, Object> state) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "ROOM_STATE_UPDATE");
        payload.put("state", state);
        payload.put("timestamp", System.currentTimeMillis());
        
        publish(CHANNEL_ROOM_PREFIX + roomId + ":state", payload);
    }

    /**
     * Публикация события о ставке
     */
    public void publishBetPlaced(Long roomId, Long userId, String username, double amount, boolean hasBoost) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "BET_PLACED");
        payload.put("roomId", roomId);
        payload.put("userId", userId);
        payload.put("username", username);
        payload.put("amount", amount);
        payload.put("hasBoost", hasBoost);
        payload.put("timestamp", System.currentTimeMillis());
        
        publish(CHANNEL_ROOM_PREFIX + roomId + ":bets", payload);
    }
}
