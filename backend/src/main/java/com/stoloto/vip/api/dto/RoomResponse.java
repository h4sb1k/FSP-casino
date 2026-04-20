package com.stoloto.vip.api.dto;

import com.stoloto.vip.domain.RoomType;
import com.stoloto.vip.domain.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * DTO для отображения информации о комнате.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private Long id;
    private String name;
    private RoomType type; // BRONZE, GOLD, DIAMOND
    private RoomStatus status; // WAITING, ACTIVE, COMPLETED
    private Long minBet;
    private Long maxBet;
    private Long entryFee;
    private Integer capacity;
    private Integer currentPlayers;
    private Instant startTime;
    private Instant endTime;
    private List<PlayerInfo> players;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlayerInfo {
        private Long userId;
        private String username;
        private Long betAmount;
        private boolean hasBoost;
        private boolean isBot;
    }
}
