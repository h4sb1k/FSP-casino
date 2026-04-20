package com.stoloto.vip.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для покупки буста.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyBoostRequest {
    private Long roomId;
    private Long boostConfigId; // ID конфигурации буста
}
