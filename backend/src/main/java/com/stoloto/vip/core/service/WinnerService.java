package com.stoloto.vip.core.service;

import com.stoloto.vip.core.entity.*;
import com.stoloto.vip.core.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class WinnerService {

    private final BetRepository betRepository;
    private final BalanceService balanceService;
    private final BoostConfigRepository boostConfigRepository;

    private final Random random = new Random();

    /**
     * Определяет победителя раунда на основе вероятностей с учетом бустов.
     * Каждый игрок получает вероятность пропорционально ставке + бонус от буста.
     */
    @Transactional
    public User determineWinner(Round round, List<Bet> bets) {
        if (bets.isEmpty()) {
            log.warn("No bets in round {}", round.getId());
            return null;
        }

        // Считаем общую взвешенную вероятность
        BigDecimal totalWeight = BigDecimal.ZERO;

        for (Bet bet : bets) {
            BigDecimal probability = calculateWinProbability(bet);
            bet.setWinProbability(probability);
            totalWeight = totalWeight.add(probability);
        }

        // Генерируем случайное число от 0 до totalWeight
        BigDecimal randomValue = new BigDecimal(random.nextDouble())
                .multiply(totalWeight);

        // Находим победителя
        BigDecimal cumulative = BigDecimal.ZERO;
        Bet winningBet = null;

        for (Bet bet : bets) {
            cumulative = cumulative.add(bet.getWinProbability());
            if (randomValue.compareTo(cumulative) <= 0) {
                winningBet = bet;
                break;
            }
        }

        // Если по какой-то причине не нашли (из-за округлений), берем последнего
        if (winningBet == null) {
            winningBet = bets.get(bets.size() - 1);
        }

        // Обновляем ставки
        betRepository.saveAll(bets);

        User winner = winningBet.getUser();
        log.info("Winner determined: round={}, winner={}, betAmount={}, probability={}",
                round.getId(), winner.getUsername(), winningBet.getAmount(), winningBet.getWinProbability());

        return winner;
    }

    /**
     * Рассчитывает вероятность победы для ставки с учетом буста.
     * Базовая вероятность = ставка / общий пул
     * Буст добавляет фиксированный процент (5%, 10%, 15% в зависимости от уровня)
     */
    public BigDecimal calculateWinProbability(Bet bet) {
        BigDecimal baseProbability = bet.getAmount()
                .divide(bet.getRound().getTotalPot(), 6, RoundingMode.HALF_UP);

        // Добавляем бонус от буста
        BigDecimal boostBonus = getBoostBonus(bet.getBoostLevel());

        return baseProbability.add(boostBonus);
    }

    /**
     * Возвращает бонус вероятности для уровня буста.
     * Уровень 1: +5% (0.05)
     * Уровень 2: +10% (0.10)
     * Уровень 3: +15% (0.15)
     */
    private BigDecimal getBoostBonus(int boostLevel) {
        if (boostLevel <= 0) {
            return BigDecimal.ZERO;
        }

        return boostConfigRepository.findByLevel(boostLevel)
                .map(BoostConfig::getProbabilityBonus)
                .orElse(new BigDecimal("0.05").multiply(BigDecimal.valueOf(boostLevel)));
    }

    /**
     * Распределяет выигрыш между победителем.
     * House edge удерживается системой.
     */
    @Transactional
    public void distributeWinnings(Round round, User winner, BigDecimal houseEdgePercent) {
        BigDecimal totalPot = round.getTotalPot();
        BigDecimal houseEdge = totalPot.multiply(houseEdgePercent.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        BigDecimal winAmount = totalPot.subtract(houseEdge);

        round.setHouseEdge(houseEdge);

        // Начисляем выигрыш победителю
        balanceService.addWin(winner.getId(), winAmount, round.getId().toString());

        log.info("Winnings distributed: round={}, winner={}, totalPot={}, houseEdge={}, winAmount={}",
                round.getId(), winner.getUsername(), totalPot, houseEdge, winAmount);
    }
}
