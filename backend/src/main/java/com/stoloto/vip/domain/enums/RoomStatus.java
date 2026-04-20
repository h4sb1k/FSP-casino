package com.stoloto.vip.domain.enums;

/**
 * Статусы игровой комнаты
 */
public enum RoomStatus {
    WAITING,            // Ожидание игроков
    ACTIVE,             // Комната активна
    ROUND_IN_PROGRESS,  // Раунд в процессе
    COMPLETED,          // Комната завершила работу
    CLOSED              // Закрыта админом
}
