package com.stoloto.vip.domain.enums;

/**
 * Статусы игровой комнаты
 */
public enum RoomStatus {
    WAITING,            // Ожидание игроков
    STARTING,           // Комната начинает раунд
    ACTIVE,             // Комната активна
    IN_GAME,            // Игра идет
    ROUND_IN_PROGRESS,  // Раунд в процессе
    COMPLETED,          // Комната завершила работу
    CLOSED              // Закрыта админом
}
