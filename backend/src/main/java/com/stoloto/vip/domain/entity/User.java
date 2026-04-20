package com.stoloto.vip.domain.entity;

import com.stoloto.vip.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * Сущность пользователя системы
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_email", columnList = "email"),
    @Index(name = "idx_users_username", columnList = "username"),
    @Index(name = "idx_users_role", columnList = "role")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private UserRole role = UserRole.USER;

    /**
     * Основной баланс в минимальных единицах (копейки/центы)
     */
    @Column(nullable = false)
    @Builder.Default
    private Long balance = 0L;

    /**
     * Бонусный баланс для покупки бустов и других бонусных операций
     */
    @Column(name = "bonus_balance", nullable = false)
    @Builder.Default
    private Long bonusBalance = 0L;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    /**
     * Проверка наличия достаточного баланса
     */
    public boolean hasSufficientBalance(Long amount) {
        return this.balance >= amount;
    }

    /**
     * Проверка наличия достаточного бонусного баланса
     */
    public boolean hasSufficientBonusBalance(Long amount) {
        return this.bonusBalance >= amount;
    }

    /**
     * Является ли пользователь администратором
     */
    public boolean isAdmin() {
        return role == UserRole.ADMIN || role == UserRole.SUPER_ADMIN;
    }

    /**
     * Является ли пользователь супер-админом
     */
    public boolean isSuperAdmin() {
        return role == UserRole.SUPER_ADMIN;
    }
}
