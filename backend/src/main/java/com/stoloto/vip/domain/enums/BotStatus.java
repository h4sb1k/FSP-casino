package com.stoloto.vip.domain.enums;

/**
 * Статусы бота
 */
public enum BotStatus {
    ACTIVE,       // Активен и может играть
    INACTIVE,     // Неактивен (отключен админом)
    IN_GAME,      // В данный момент играет в комнате
    MAINTENANCE   // На обслуживании
}
