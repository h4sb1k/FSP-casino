package com.stoloto.vip.api.auth;

import com.stoloto.vip.api.dto.LoginRequest;
import com.stoloto.vip.api.dto.RegisterRequest;
import com.stoloto.vip.api.dto.AuthResponse;
import com.stoloto.vip.core.entity.User;
import com.stoloto.vip.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;

/**
 * Контроллер для аутентификации и регистрации пользователей.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Настроить для prod
public class AuthController {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Регистрация нового пользователя.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .balance(BigDecimal.valueOf(1000))
                .bonusBalance(BigDecimal.ZERO)
                .reservedBalance(BigDecimal.ZERO)
                .build();
        userRepository.save(user);
        
        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken("mock-access-token")
                .refreshToken("mock-refresh-token")
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
        var user = userRepository.findByEmail(request.getEmail())
                .orElseGet(() -> {
                    var newUser = User.builder()
                            .username("demo")
                            .email(request.getEmail())
                            .passwordHash(passwordEncoder.encode("password"))
                            .balance(BigDecimal.valueOf(1000))
                            .bonusBalance(BigDecimal.ZERO)
                            .reservedBalance(BigDecimal.ZERO)
                            .build();
                    return newUser;
                });
        
        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken("mock-access-token")
                .refreshToken("mock-refresh-token")
                .userId(user.getId() != null ? user.getId() : 1L)
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
        var user = new User();
        user.setId(1L);
        user.setUsername("demo");
        user.setEmail("demo@example.com");
        user.setBalance(BigDecimal.valueOf(1000));
        user.setBonusBalance(BigDecimal.ZERO);
        
        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken("mock-access-token")
                .refreshToken("mock-refresh-token")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .balance(user.getBalance())
                .bonusBalance(user.getBonusBalance())
                .build());
    }
}
