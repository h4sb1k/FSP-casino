package com.stoloto.vip.core.service;

import com.stoloto.vip.core.entity.*;
import com.stoloto.vip.core.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoostService {

    private final UserRepository userRepository;
    private final BoostConfigRepository boostConfigRepository;
    private final BalanceService balanceService;
    private final UserBoostRepository userBoostRepository;

    private static final int MAX_BOOSTS_PER_ROUND = 3;

    /**
     * Покупает буст для пользователя.
     * Стоимость растет: 1-й буст = базовая цена, 2-й = 2x, 3-й = 3x
     */
    @Transactional
    public UserBoost purchaseBoost(Long userId, Integer level) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        BoostConfig config = boostConfigRepository.findByLevel(level)
                .orElseThrow(() -> new IllegalArgumentException("Boost level not available: " + level));

        if (!config.getActive()) {
            throw new IllegalStateException("Boost level is not active");
        }

        // Проверяем сколько бустов уже использовано в текущем раунде
        long usedBoostsCount = userBoostRepository.countByUserIdAndUsedTrue(userId);
        if (usedBoostsCount >= MAX_BOOSTS_PER_ROUND) {
            throw new IllegalStateException("Maximum boosts per round reached (" + MAX_BOOSTS_PER_ROUND + ")");
        }

        // Рассчитываем стоимость с учетом множителя
        BigDecimal cost = config.getCost().multiply(BigDecimal.valueOf(usedBoostsCount + 1));

        // Списываем средства с бонусного баланса
        if (user.getBonusBalance().compareTo(cost) < 0) {
            throw new IllegalArgumentException("Insufficient bonus balance. Required: " + cost);
        }

        user.setBonusBalance(user.getBonusBalance().subtract(cost));
        userRepository.save(user);

        // Создаем запись о бусте
        UserBoost userBoost = UserBoost.builder()
                .user(user)
                .boostConfig(config)
                .used(false)
                .build();

        userBoostRepository.save(userBoost);

        log.info("Boost purchased: user={}, level={}, cost={}", userId, level, cost);
        return userBoost;
    }

    /**
     * Применяет буст к ставке в раунде.
     */
    @Transactional
    public void applyBoostToBet(UserBoost userBoost, Bet bet) {
        if (userBoost.getUsed()) {
            throw new IllegalStateException("Boost already used");
        }

        userBoost.setUsed(true);
        userBoost.setRound(bet.getRound());
        bet.setBoostLevel(userBoost.getBoostConfig().getLevel());

        userBoostRepository.save(userBoost);

        log.info("Boost applied: userId={}, boostLevel={}, betId={}",
                userBoost.getUser().getId(), userBoost.getBoostConfig().getLevel(), bet.getId());
    }

    /**
     * Проверяет доступные бусты пользователя.
     */
    public List<UserBoost> getAvailableBoosts(Long userId) {
        return userBoostRepository.findByUserIdAndUsedFalse(userId);
    }

    /**
     * Возвращает конфигурации бустов.
     */
    public List<BoostConfig> getAllActiveBoostConfigs() {
        return boostConfigRepository.findByActiveTrue();
    }
}
