package com.stoloto.vip.realtime.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stoloto.vip.api.dto.BuyBoostRequest;
import com.stoloto.vip.api.dto.JoinRoomRequest;
import com.stoloto.vip.realtime.WsMessage;
import com.stoloto.vip.service.RoomService;
import com.stoloto.vip.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Обработчик WebSocket соединений для real-time уведомлений.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GameWebSocketHandler extends TextWebSocketHandler {
    
    private final ObjectMapper objectMapper;
    private final RoomService roomService;
    private final UserService userService;
    
    // Хранение активных сессий: userId -> Session
    private final Map<Long, WebSocketSession> activeSessions = new ConcurrentHashMap<>();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("New WebSocket connection established: {}", session.getId());
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            var wsMessage = objectMapper.readValue(message.getPayload(), WsMessage.class);
            var userDetails = (UserDetails) session.getAttributes().get("user");
            
            if (userDetails == null) {
                sendError(session, "Unauthorized");
                return;
            }
            
            var user = userService.findByEmail(userDetails.getUsername());
            
            switch (wsMessage.getType()) {
                case "JOIN_ROOM" -> handleJoinRoom(session, wsMessage, user);
                case "LEAVE_ROOM" -> handleLeaveRoom(session, wsMessage, user);
                case "PLACE_BET" -> handlePlaceBet(session, wsMessage, user);
                case "BUY_BOOST" -> handleBuyBoost(session, wsMessage, user);
                case "SUBSCRIBE_ROUND" -> handleSubscribeRound(session, wsMessage, user);
                default -> sendError(session, "Unknown message type: " + wsMessage.getType());
            }
        } catch (Exception e) {
            log.error("Error handling WebSocket message", e);
            sendError(session, "Internal error: " + e.getMessage());
        }
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Удаляем сессию из активных
        activeSessions.entrySet().removeIf(entry -> entry.getValue().equals(session));
        log.info("WebSocket connection closed: {} with status {}", session.getId(), status);
    }
    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket transport error for session {}: {}", session.getId(), exception.getMessage());
    }
    
    private void handleJoinRoom(WebSocketSession session, WsMessage wsMessage, com.stoloto.vip.domain.User user) {
        try {
            var payload = objectMapper.convertValue(wsMessage.getPayload(), JoinRoomRequest.class);
            var room = roomService.joinRoom(payload.getRoomId(), payload.getBetAmount(), user);
            
            // Сохраняем сессию для push-уведомлений
            activeSessions.put(user.getId(), session);
            
            sendSuccess(session, "ROOM_JOINED", room);
            log.info("User {} joined room {}", user.getId(), payload.getRoomId());
        } catch (Exception e) {
            sendError(session, "Failed to join room: " + e.getMessage());
        }
    }
    
    private void handleLeaveRoom(WebSocketSession session, WsMessage wsMessage, com.stoloto.vip.domain.User user) {
        try {
            var roomId = ((Number) wsMessage.getPayload().get("roomId")).longValue();
            var room = roomService.leaveRoom(roomId, user);
            
            sendSuccess(session, "ROOM_LEFT", room);
            log.info("User {} left room {}", user.getId(), roomId);
        } catch (Exception e) {
            sendError(session, "Failed to leave room: " + e.getMessage());
        }
    }
    
    private void handlePlaceBet(WebSocketSession session, WsMessage wsMessage, com.stoloto.vip.domain.User user) {
        // Ставка фиксируется при входе в комнату, это событие для подтверждения
        sendSuccess(session, "BET_CONFIRMED", Map.of("message", "Bet is already placed on join"));
    }
    
    private void handleBuyBoost(WebSocketSession session, WsMessage wsMessage, com.stoloto.vip.domain.User user) {
        try {
            var payload = objectMapper.convertValue(wsMessage.getPayload(), BuyBoostRequest.class);
            var room = roomService.buyBoost(payload.getRoomId(), payload.getBoostConfigId(), user);
            
            sendSuccess(session, "BOOST_PURCHASED", room);
            log.info("User {} bought boost in room {}", user.getId(), payload.getRoomId());
        } catch (Exception e) {
            sendError(session, "Failed to buy boost: " + e.getMessage());
        }
    }
    
    private void handleSubscribeRound(WebSocketSession session, WsMessage wsMessage, com.stoloto.vip.domain.User user) {
        // Подписка на обновления раунда
        var roundId = ((Number) wsMessage.getPayload().get("roundId")).longValue();
        sendSuccess(session, "SUBSCRIBED_TO_ROUND", Map.of("roundId", roundId));
    }
    
    /**
     * Отправить успешный ответ клиенту.
     */
    public void sendSuccess(WebSocketSession session, String type, Object data) {
        try {
            var response = WsMessage.builder()
                    .type(type)
                    .status("SUCCESS")
                    .payload(data)
                    .build();
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
        } catch (Exception e) {
            log.error("Error sending success message", e);
        }
    }
    
    /**
     * Отправить ошибку клиенту.
     */
    public void sendError(WebSocketSession session, String errorMessage) {
        try {
            var response = WsMessage.builder()
                    .type("ERROR")
                    .status("ERROR")
                    .payload(Map.of("message", errorMessage))
                    .build();
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
        } catch (Exception e) {
            log.error("Error sending error message", e);
        }
    }
    
    /**
     * Отправить уведомление всем игрокам в комнате.
     */
    public void broadcastToRoom(Long roomId, String type, Object data) {
        var message = WsMessage.builder()
                .type(type)
                .status("BROADCAST")
                .payload(data)
                .build();
        
        try {
            var jsonMessage = new TextMessage(objectMapper.writeValueAsString(message));
            
            activeSessions.values().forEach(session -> {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(jsonMessage);
                    } catch (Exception e) {
                        log.error("Error broadcasting to session {}", session.getId(), e);
                    }
                }
            });
        } catch (Exception e) {
            log.error("Error creating broadcast message", e);
        }
    }
    
    /**
     * Получить активную сессию пользователя.
     */
    public WebSocketSession getUserSession(Long userId) {
        return activeSessions.get(userId);
    }
}
