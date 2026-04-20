package com.stoloto.vip.api.user;

import com.stoloto.vip.api.dto.BalanceResponse;
import com.stoloto.vip.api.dto.TransactionResponse;
import com.stoloto.vip.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для управления профилем пользователя и балансом.
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserService userService;
    
    /**
     * Получить профиль текущего пользователя.
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        var user = userService.findByEmail(userDetails.getUsername());
        return ResponseEntity.ok(user);
    }
    
    /**
     * Получить текущий баланс пользователя.
     */
    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> getBalance(
            @AuthenticationPrincipal UserDetails userDetails) {
        var user = userService.findByEmail(userDetails.getUsername());
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
        var user = userService.findByEmail(userDetails.getUsername());
        var transactions = userService.getUserTransactions(user.getId(), limit != null ? limit : 50);
        return ResponseEntity.ok(transactions);
    }
    
    /**
     * Получить историю игр пользователя.
     */
    @GetMapping("/history")
    public ResponseEntity<?> getGameHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Integer limit) {
        var user = userService.findByEmail(userDetails.getUsername());
        var history = userService.getUserGameHistory(user.getId(), limit != null ? limit : 20);
        return ResponseEntity.ok(history);
    }
}
