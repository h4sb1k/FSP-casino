package com.stoloto.vip.realtime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO для WebSocket сообщений.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WsMessage {
    private String type;      // JOIN_ROOM, ROUND_START, ROUND_RESULT, ERROR и т.д.
    private String status;    // SUCCESS, ERROR, BROADCAST
    private Object payload;   // Данные сообщения
    private Long timestamp;   // Время отправки
    
    public WsMessage(String type, String status, Object payload) {
        this.type = type;
        this.status = status;
        this.payload = payload;
        this.timestamp = System.currentTimeMillis();
    }
}
