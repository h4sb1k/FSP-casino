package com.stoloto.vip.api.user;

import com.stoloto.vip.api.dto.BalanceResponse;
import com.stoloto.vip.api.dto.TransactionResponse;
import com.stoloto.vip.core.entity.User;
import com.stoloto.vip.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Контроллер для управления профилем пользователя и балансом.
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserRepository userRepository;
    
    /**
     * Получить профиль текущего пользователя.
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        var user = userRepository.findByEmail(userDetails.getUsername())
                .orElse(new User());
        return ResponseEntity.ok(user);
    }
    
    /**
     * Получить текущий баланс пользователя.
     */
    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> getBalance(
            @AuthenticationPrincipal UserDetails userDetails) {
        var user = userRepository.findByEmail(userDetails.getUsername())
                .orElse(new User());
        var balance = BalanceResponse.builder()
                .userId(user.getId())
                .balance(user.getBalance())
                .bonusBalance(user.getBonusBalance())
                .reservedBalance(user.getReservedBalance())
                .build();
        return ResponseEntity.ok(balance);
    }
    
    /**
     * Получить историю транзакций пользователя.
     */
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Integer limit) {
        // Заглушка - возвращаем пустой список
        return ResponseEntity.ok(List.of());
    }
    
    /**
     * Получить историю игр пользователя.
     */
    @GetMapping("/history")
    public ResponseEntity<?> getGameHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Integer limit) {
        // Заглушка - возвращаем пустой список
        return ResponseEntity.ok(List.of());
    }
}
