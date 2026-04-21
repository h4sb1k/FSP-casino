package com.stoloto.vip.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO для отображения баланса пользователя.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceResponse {
    private Long userId;
    private BigDecimal balance; // Основные монеты
    private BigDecimal bonusBalance; // Бонусные баллы (для бустов)
    private BigDecimal reservedBalance; // Зарезервировано на текущие игры
}
