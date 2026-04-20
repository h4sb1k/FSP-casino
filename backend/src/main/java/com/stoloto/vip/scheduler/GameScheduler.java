package com.stoloto.vip.scheduler;

import com.stoloto.vip.domain.entity.Room;
import com.stoloto.vip.domain.entity.Round;
import com.stoloto.vip.domain.enums.RoomStatus;
import com.stoloto.vip.domain.enums.RoundStatus;
import com.stoloto.vip.repository.RoomRepository;
import com.stoloto.vip.repository.RoundRepository;
import com.stoloto.vip.service.game.RoomService;
import com.stoloto.vip.service.balance.BalanceService;
import com.stoloto.vip.realtime.redis.RedisPubSubPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Планировщик для управления таймерами комнат и раундов
 * Все операции выполняются на стороне сервера для защиты от абуза
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GameScheduler {

    private final RoomRepository roomRepository;
    private final RoundRepository roundRepository;
    private final RoomService roomService;
    private final BalanceService balanceService;
    private final RedisPubSubPublisher redisPublisher;

    /**
     * Проверка комнат со статусом STARTING каждые 5 секунд
     * Если прошло 60 секунд - запуск раунда
     */
    @Scheduled(fixedRate = 5000)
    @Transactional
    public void checkStartingRooms() {
        log.debug("Checking starting rooms...");
        
        Instant now = Instant.now();
        Instant cutoffTime = now.minusSeconds(60); // Таймер ожидания 1 минута
        
        List<Room> startingRooms = roomRepository.findByStatusAndStartTimeBefore(
                RoomStatus.STARTING, cutoffTime);
        
        for (Room room : startingRooms) {
            try {
                log.info("Starting round for room {} (timer expired)", room.getId());
                
                // Запуск раунда
                Round round = roomService.startRound(room);
                
                if (round != null) {
                    // Публикация события в Redis
                    redisPublisher.publishRoundStart(room.getId(), round.getId());
                }
            } catch (Exception e) {
                log.error("Error starting round for room {}", room.getId(), e);
                // Откат средств игрокам при ошибке
                rollbackRoomBets(room);
            }
        }
    }

    /**
     * Проверка активных раундов
     * Для демонстрации - раунд длится 30 секунд
     * В продакшене длительность может быть настроена через админку
     */
    @Scheduled(fixedRate = 5000)
    @Transactional
    public void checkActiveRounds() {
        log.debug("Checking active rounds...");
        
        Instant now = Instant.now();
        Instant cutoffTime = now.minusSeconds(30); // Длительность раунда 30 секунд
        
        List<Round> activeRounds = roundRepository.findByStatusAndStartedAtBefore(
                RoundStatus.IN_PROGRESS, cutoffTime);
        
        for (Round round : activeRounds) {
            try {
                log.info("Completing round {}", round.getId());
                
                // Предупреждение об окончании (за 5 секунд до конца)
                // Можно отправить через WebSocket
                
                // Определение победителя
                RoomService.RoundResult result = roomService.determineWinner(round);
                
                if (result != null) {
                    // Обновление статуса раунда
                    round.setStatus(RoundStatus.COMPLETED);
                    round.setCompletedAt(now);
                    round.setWinnerId(result.getWinnerId());
                    round.setWinAmount(result.getWinAmount());
                    round.setRngSeedProof(result.getRngSeedProof());
                    roundRepository.save(round);
                    
                    // Обновление статуса комнаты
                    Room room = round.getRoom();
                    room.setStatus(RoomStatus.WAITING);
                    room.setCurrentPlayers(0);
                    room.setCurrentRoundId(null);
                    room.setStartTime(null);
                    roomRepository.save(room);
                    
                    // Публикация результатов в Redis
                    redisPublisher.publishRoundResult(room.getId(), round.getId(), result);
                    
                    log.info("Round {} completed. Winner: {}", round.getId(), 
                             result.isBotWinner() ? "BOT" : result.getWinnerId());
                }
            } catch (Exception e) {
                log.error("Error completing round {}", round.getId(), e);
                // Откат средств при ошибке завершения раунда
                rollbackRoundBets(round);
            }
        }
    }

    /**
     * Проверка зависших комнат (на случай сбоев)
     * Комнаты в статусе WAITING без игроков более 5 минут будут удалены
     */
    @Scheduled(fixedRate = 60000) // Каждую минуту
    @Transactional
    public void cleanupStaleRooms() {
        log.debug("Cleaning up stale rooms...");
        
        Instant now = Instant.now();
        Instant cutoffTime = now.minusMinutes(5);
        
        List<Room> staleRooms = roomRepository.findByStatusAndCreatedAtBefore(
                RoomStatus.WAITING, cutoffTime);
        
        for (Room room : staleRooms) {
            if (room.getCurrentPlayers() == 0) {
                log.info("Removing stale empty room {}", room.getId());
                roomRepository.delete(room);
            }
        }
    }

    /**
     * Откат ставок при ошибке запуска раунда
     */
    private void rollbackRoomBets(Room room) {
        log.warn("Rolling back bets for room {} due to error", room.getId());
        
        // TODO: Получить все транзакции для этой комнаты и сделать rollback
        // List<Transaction> transactions = transactionRepository.findByRoomIdAndStatus(room.getId(), TransactionStatus.PENDING);
        // for (Transaction tx : transactions) {
        //     balanceService.rollbackTransaction(tx.getId().toString(), "ROOM_START_ERROR");
        // }
        
        room.setStatus(RoomStatus.WAITING);
        room.setStartTime(null);
        roomRepository.save(room);
    }

    /**
     * Откат ставок при ошибке завершения раунда
     */
    private void rollbackRoundBets(Round round) {
        log.warn("Rolling back bets for round {} due to error", round.getId());
        
        // TODO: Получить все транзакции для этого раунда и сделать rollback
        // List<Transaction> transactions = transactionRepository.findByRoundIdAndStatus(round.getId(), TransactionStatus.PENDING);
        // for (Transaction tx : transactions) {
        //     balanceService.rollbackTransaction(tx.getId().toString(), "ROUND_COMPLETE_ERROR");
        // }
        
        Room room = round.getRoom();
        room.setStatus(RoomStatus.WAITING);
        room.setCurrentRoundId(null);
        roomRepository.save(room);
    }
}
