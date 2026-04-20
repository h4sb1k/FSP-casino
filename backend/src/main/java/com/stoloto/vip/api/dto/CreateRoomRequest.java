package com.stoloto.vip.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для создания новой игровой комнаты.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoomRequest {
    private String gameType; // Тип игры (будущая интеграция)
    private Long minBet;
    private Long maxBet;
    private Integer capacity; // Максимум 10
    private Long entryFee;
}
