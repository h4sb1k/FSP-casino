package com.stoloto.vip.bot;

import com.stoloto.vip.domain.Room;
import com.stoloto.vip.domain.User;
import com.stoloto.vip.repository.BotConfigRepository;
import com.stoloto.vip.repository.RoomRepository;
import com.stoloto.vip.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

/**
 * Сервис управления ботами.
 * Боты заполняют пустые места в комнатах для создания реалистичной атмосферы.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BotService {
    
    private final RoomRepository roomRepository;
    private final BotConfigRepository botConfigRepository;
    private final RoomService roomService;
    
    private final Random random = new Random();
    
    /**
     * Проверка комнат и добавление ботов при необходимости.
     * Запускается каждые 5 секунд.
     */
    @Scheduled(fixedRate = 5000)
    @Transactional
    public void fillRoomsIfNeeded() {
        // Находим все комнаты со статусом WAITING, где есть свободные места
        var waitingRooms = roomRepository.findByStatusWaiting();
        
        for (Room room : waitingRooms) {
            int currentPlayers = room.getPlayers().size();
            int capacity = room.getCapacity();
            
            if (currentPlayers < capacity) {
                int botsToAdd = capacity - currentPlayers;
                
                // Добавляем ботов с небольшой задержкой для реалистичности
                for (int i = 0; i < botsToAdd; i++) {
                    try {
                        Thread.sleep(random.nextInt(500) + 100); // 100-600ms задержка
                        addBotToRoom(room);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Добавить бота в комнату.
     */
    @Transactional
    public void addBotToRoom(Room room) {
        // Проверяем, что комната ещё существует и активна
        var freshRoom = roomRepository.findById(room.getId());
        if (freshRoom.isEmpty() || freshRoom.get().getStatus() != com.stoloto.vip.domain.RoomStatus.WAITING) {
            return;
        }
        
        // Получаем случайную конфигурацию бота
        var botConfigs = botConfigRepository.findAll();
        if (botConfigs.isEmpty()) {
            log.warn("No bot configurations available");
            return;
        }
        
        var botConfig = botConfigs.get(random.nextInt(botConfigs.size()));
        
        // Создаём виртуального бота
        var botUser = User.builder()
                .username(botConfig.getName())
                .email("bot_" + botConfig.getId() + "@system.local")
                .password("")
                .balance(1_000_000L) // У ботов бесконечные деньги
                .bonusBalance(0L)
                .reservedBalance(0L)
                .isBot(true)
                .build();
        
        // Присоединяем бота к комнате
        try {
            roomService.joinRoom(room.getId(), room.getMinBet(), botUser);
            log.info("Bot {} joined room {}", botConfig.getName(), room.getId());
        } catch (Exception e) {
            log.error("Failed to add bot {} to room {}", botConfig.getName(), room.getId(), e);
        }
    }
    
    /**
     * Удалить ботов из завершённой комнаты (очистка).
     */
    @Transactional
    public void cleanupBotsFromRoom(Long roomId) {
        // Боты не удаляются явно, они просто не участвуют в следующих раундах
        // Очистка происходит автоматически при создании новой комнаты
        log.debug("Cleanup bots from room {}", roomId);
    }
}
