package com.stoloto.vip.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для присоединения к комнате.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinRoomRequest {
    private Long roomId;
    private Long betAmount; // Фиксированная ставка комнаты или выбранная пользователем в пределах лимита
}
