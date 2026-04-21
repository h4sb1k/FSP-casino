package com.stoloto.vip.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Упрощенный планировщик (заглушка)
 */
@Component
@Slf4j
public class GameScheduler {

    /**
     * Заглушка для проверки комнат
     */
    public void checkStartingRooms() {
        log.debug("Scheduler stub - not implemented");
    }
}
