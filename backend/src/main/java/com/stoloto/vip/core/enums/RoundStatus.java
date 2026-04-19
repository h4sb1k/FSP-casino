package com.stoloto.vip.core.enums;

public enum RoundStatus {
    PENDING,      // Раунд создан, ожидает начала
    BETTING,      // Прием ставок
    PROCESSING,   // Обработка результатов
    COMPLETED,    // Раунд завершен
    CANCELLED     // Раунд отменен
}
