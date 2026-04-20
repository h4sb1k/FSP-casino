package com.stoloto.vip.service.game;

import com.stoloto.vip.domain.entity.Room;
import com.stoloto.vip.domain.entity.Round;
import com.stoloto.vip.domain.entity.User;
import com.stoloto.vip.domain.enums.RoomStatus;
import com.stoloto.vip.repository.RoomRepository;
import com.stoloto.vip.repository.RoundRepository;
import com.stoloto.vip.rng.RngService;
import com.stoloto.vip.service.balance.BalanceService;
import com.stoloto.vip.service.audit.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

/**
 * Сервис управления игровыми комнатами и раундами
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoundRepository roundRepository;
    private final RngService rngService;
    private final BalanceService balanceService;
    private final AuditService auditService;

    /**
     * Создание новой комнаты с указанными параметрами
     */
    @Transactional
    public Room createRoom(Room.RoomType type, User creator) {
        log.info("Creating new {} room for user {}", type, creator.getUsername());
        
        Room room = Room.builder()
                .type(type)
                .status(RoomStatus.WAITING)
                .capacity(10)
                .currentPlayers(0)
                .createdAt(Instant.now())
                .build();
        
        // Установка параметров в зависимости от типа комнаты
        switch (type) {
            case BRONZE:
                room.setName("Bronze Room #" + System.currentTimeMillis());
                room.setMinBet(new BigDecimal("10"));
                room.setMaxBet(new BigDecimal("100"));
                break;
            case GOLD:
                room.setName("Gold Room #" + System.currentTimeMillis());
                room.setMinBet(new BigDecimal("100"));
                room.setMaxBet(new BigDecimal("500"));
                break;
            case DIAMOND:
                room.setName("Diamond Room #" + System.currentTimeMillis());
                room.setMinBet(new BigDecimal("500"));
                room.setMaxBet(new BigDecimal("2000"));
                break;
        }
        
        Room savedRoom = roomRepository.save(room);
        
        auditService.logRoomCreated(savedRoom.getId(), creator.getId(), type.name());
        
        return savedRoom;
    }

    /**
     * Присоединение игрока к комнате
     */
    @Transactional
    public Room joinRoom(Long roomId, Long userId, BigDecimal betAmount, String idempotencyKey) {
        log.info("User {} joining room {} with bet {}", userId, roomId, betAmount);
        
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found: " + roomId));
        
        if (room.getStatus() != RoomStatus.WAITING) {
            throw new RuntimeException("Room is not accepting players: " + room.getStatus());
        }
        
        if (room.getCurrentPlayers() >= room.getCapacity()) {
            throw new RuntimeException("Room is full");
        }
        
        // Проверка ставки
        if (betAmount.compareTo(room.getMinBet()) < 0 || betAmount.compareTo(room.getMaxBet()) > 0) {
            throw new RuntimeException("Bet amount out of range: [" + room.getMinBet() + ", " + room.getMaxBet() + "]");
        }
        
        // Резервирование средств
        balanceService.reserveForBet(userId, betAmount, roomId, null, idempotencyKey);
        
        // Добавление игрока в комнату
        room.incrementPlayers();
        
        // Запуск таймера если это первый игрок
        if (room.getCurrentPlayers() == 1) {
            room.setStartTime(Instant.now());
            room.setStatus(RoomStatus.STARTING);
        }
        
        Room savedRoom = roomRepository.save(room);
        
        auditService.logPlayerJoinedRoom(roomId, userId, betAmount.doubleValue());
        
        return savedRoom;
    }

    /**
     * Покупка и активация буста
     */
    @Transactional
    public void activateBoost(Long roomId, Long userId, Long boostConfigId, String idempotencyKey) {
        log.info("User {} activating boost {} in room {}", userId, boostConfigId, roomId);
        
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        
        if (room.getStatus() != RoomStatus.WAITING && room.getStatus() != RoomStatus.STARTING) {
            throw new RuntimeException("Cannot activate boost: room status is " + room.getStatus());
        }
        
        // TODO: Получить конфиг буста и зарезервировать стоимость
        // balanceService.reserveForBoost(userId, boostCost, roomId, null, idempotencyKey);
        
        auditService.logBoostActivated(roomId, userId, boostConfigId);
    }

    /**
     * Запуск раунда (вызывается планировщиком)
     */
    @Transactional
    public Round startRound(Room room) {
        log.info("Starting round in room {}", room.getId());
        
        if (room.getCurrentPlayers() == 0) {
            log.warn("Cannot start round: no players in room {}", room.getId());
            return null;
        }
        
        // Заполнение ботами если нужно
        fillWithBots(room);
        
        // Создание раунда
        Round round = Round.builder()
                .room(room)
                .status(com.stoloto.vip.domain.enums.RoundStatus.IN_PROGRESS)
                .startedAt(Instant.now())
                .serverSeed(rngService.generateServerSeed())
                .build();
        
        Round savedRound = roundRepository.save(round);
        
        room.setStatus(RoomStatus.IN_GAME);
        room.setCurrentRoundId(savedRound.getId());
        roomRepository.save(room);
        
        auditService.logRoundStarted(savedRound.getId(), room.getId(), room.getCurrentPlayers());
        
        return savedRound;
    }

    /**
     * Заполнение комнаты ботами до максимальной вместимости
     */
    private void fillWithBots(Room room) {
        int botsNeeded = room.getCapacity() - room.getCurrentPlayers();
        
        if (botsNeeded <= 0) {
            return;
        }
        
        log.info("Filling room {} with {} bots", room.getId(), botsNeeded);
        
        // TODO: Реализовать добавление ботов
        // botService.addBotsToRoom(room, botsNeeded);
        
        room.setCurrentPlayers(room.getCapacity());
        roomRepository.save(room);
    }

    /**
     * Определение победителя раунда
     */
    @Transactional
    public RoundResult determineWinner(Round round) {
        log.info("Determining winner for round {}", round.getId());
        
        // Получение всех участников с их ставками и бустами
        List<RoundParticipant> participants = getRoundParticipants(round);
        
        if (participants.isEmpty()) {
            log.warn("No participants in round {}", round.getId());
            return null;
        }
        
        // Расчет весов для каждого участника
        List<WeightedParticipant> weightedParticipants = participants.stream()
                .map(p -> {
                    BigDecimal weight = calculateWeight(p.getBetAmount(), p.hasBoost());
                    return new WeightedParticipant(p, weight);
                })
                .toList();
        
        // Генерация случайного числа с аудитом
        String clientSeed = rngService.generateClientSeed();
        String combinedSeed = rngService.combineSeeds(round.getServerSeed(), clientSeed, round.getId());
        double randomValue = rngService.getRandomValue(combinedSeed);
        
        // Выбор победителя на основе весов
        WeightedParticipant winner = selectWeightedRandom(weightedParticipants, randomValue);
        
        // Расчет выигрыша
        BigDecimal totalPool = calculateTotalPool(participants);
        BigDecimal winAmount = totalPool.multiply(BigDecimal.valueOf(0.95)); // 5% комиссия платформы
        
        // Обновление баланса победителя
        if (!winner.getParticipant().isBot()) {
            balanceService.creditWin(winner.getParticipant().getUserId(), winAmount, round.getId());
        } else {
            // Выигрыш бота остается в системе
            balanceService.creditHouse(winAmount, round.getId());
        }
        
        // Запись результатов
        Round result = finalizeRound(round, winner, winAmount, combinedSeed);
        
        auditService.logRoundCompleted(
                round.getId(), 
                winner.getParticipant().getUserId(),
                winAmount.doubleValue(),
                combinedSeed
        );
        
        return mapToResult(result, winner, winAmount);
    }

    /**
     * Расчет веса участника с учетом буста
     */
    private BigDecimal calculateWeight(BigDecimal betAmount, boolean hasBoost) {
        if (hasBoost) {
            // TODO: Получить процент буста из конфига
            int boostPercent = 10; // Заглушка
            return betAmount.multiply(BigDecimal.valueOf(1 + boostPercent / 100.0));
        }
        return betAmount;
    }

    /**
     * Выбор победителя методом взвешенной случайности
     */
    private WeightedParticipant selectWeightedRandom(List<WeightedParticipant> participants, double randomValue) {
        BigDecimal totalWeight = participants.stream()
                .map(WeightedParticipant::getWeight)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal currentSum = BigDecimal.ZERO;
        BigDecimal target = totalWeight.multiply(BigDecimal.valueOf(randomValue));
        
        for (WeightedParticipant participant : participants) {
            currentSum = currentSum.add(participant.getWeight());
            if (currentSum.compareTo(target) >= 0) {
                return participant;
            }
        }
        
        return participants.get(participants.size() - 1);
    }

    /**
     * Подсчет общего пула ставок
     */
    private BigDecimal calculateTotalPool(List<RoundParticipant> participants) {
        return participants.stream()
                .map(RoundParticipant::getBetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Вспомогательные классы
     */
    @lombok.Data
    @lombok.Builder
    private static class RoundParticipant {
        private Long userId;
        private String username;
        private BigDecimal betAmount;
        private boolean hasBoost;
        private boolean isBot;
    }

    @lombok.Data
    private static class WeightedParticipant {
        private final RoundParticipant participant;
        private final BigDecimal weight;
    }

    @lombok.Data
    @lombok.Builder
    public static class RoundResult {
        private Long roundId;
        private Long winnerId;
        private String winnerName;
        private boolean isBotWinner;
        private BigDecimal winAmount;
        private String rngSeedProof;
    }

    // TODO: Реализовать методы getRoundParticipants, finalizeRound, mapToResult
}
