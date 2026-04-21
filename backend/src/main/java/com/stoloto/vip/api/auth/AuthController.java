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
        var user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setBalance(BigDecimal.valueOf(1000));
        user.setBonusBalance(BigDecimal.ZERO);
        user.setReservedBalance(BigDecimal.ZERO);
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
                    var newUser = new User();
                    newUser.setUsername("demo");
                    newUser.setEmail(request.getEmail());
                    newUser.setPassword(passwordEncoder.encode("password"));
                    newUser.setBalance(BigDecimal.valueOf(1000));
                    newUser.setBonusBalance(BigDecimal.ZERO);
                    newUser.setReservedBalance(BigDecimal.ZERO);
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
