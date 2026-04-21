package com.stoloto.vip.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO ответа с JWT токеном и информацией о пользователе.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String username;
    private String email;
    private BigDecimal balance;
    private BigDecimal bonusBalance;
}
