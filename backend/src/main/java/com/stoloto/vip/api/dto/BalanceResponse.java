package com.stoloto.vip.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для отображения баланса пользователя.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceResponse {
    private Long userId;
    private Long balance; // Основные монеты
    private Long bonusBalance; // Бонусные баллы (для бустов)
    private Long reservedBalance; // Зарезервировано на текущие игры
}
