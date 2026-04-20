package com.stoloto.vip.domain.enums;

/**
 * Статусы игрового раунда
 */
public enum RoundStatus {
    WAITING_FOR_PLAYERS,  // Ожидание набора игроков
    BETTING_OPEN,         // Прием ставок открыт
    BETTING_CLOSED,       // Прием ставок закрыт
    IN_PROGRESS,          // Раунд в процессе (игра идет)
    COMPLETED,            // Раунд завершен
    CANCELLED             // Раунд отменен
}
