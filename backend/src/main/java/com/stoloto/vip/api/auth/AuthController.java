package com.stoloto.vip.api.auth;

import com.stoloto.vip.api.dto.LoginRequest;
import com.stoloto.vip.api.dto.RegisterRequest;
import com.stoloto.vip.api.dto.AuthResponse;
import com.stoloto.vip.service.UserService;
import com.stoloto.vip.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Контроллер для аутентификации и регистрации пользователей.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Настроить для prod
public class AuthController {
    
    private final UserService userService;
    private final JwtService jwtService;
    
    /**
     * Регистрация нового пользователя.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        var user = userService.register(request.getUsername(), request.getEmail(), request.getPassword());
        var tokens = jwtService.generateTokens(user);
        
        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(tokens.getAccessToken())
                .refreshToken(tokens.getRefreshToken())
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .balance(user.getBalance())
                .bonusBalance(user.getBonusBalance())
                .build());
    }
    
    /**
     * Аутентификация пользователя.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        var user = userService.authenticate(request.getEmail(), request.getPassword());
        var tokens = jwtService.generateTokens(user);
        
        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(tokens.getAccessToken())
                .refreshToken(tokens.getRefreshToken())
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .balance(user.getBalance())
                .bonusBalance(user.getBonusBalance())
                .build());
    }
    
    /**
     * Обновление access токена по refresh токену.
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestParam String refreshToken) {
        var user = jwtService.refreshAccessToken(refreshToken);
        var tokens = jwtService.generateTokens(user);
        
        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(tokens.getAccessToken())
                .refreshToken(tokens.getRefreshToken())
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .balance(user.getBalance())
                .bonusBalance(user.getBonusBalance())
                .build());
    }
}
