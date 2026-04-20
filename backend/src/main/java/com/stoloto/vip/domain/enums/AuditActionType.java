package com.stoloto.vip.domain.enums;

/**
 * Типы действий для аудита
 */
public enum AuditActionType {
    // Пользовательские действия
    USER_LOGIN,
    USER_LOGOUT,
    USER_REGISTER,
    
    // Действия с комнатами
    ROOM_CREATED,
    ROOM_STARTED,
    ROOM_CLOSED,
    
    // Действия с раундами
    ROUND_STARTED,
    ROUND_COMPLETED,
    ROUND_CANCELLED,
    
    // Ставки
    BET_PLACED,
    BET_SETTLED,
    BET_CANCELLED,
    
    // Бусты
    BOOST_ACTIVATED,
    BOOST_PURCHASED,
    
    // Боты
    BOT_JOINED,
    BOT_LEFT,
    BOT_ACTION,
    
    // Финансы
    BALANCE_CHANGED,
    TRANSACTION_CREATED,
    
    // Административные действия
    ADMIN_CONFIG_CHANGE,
    ADMIN_USER_ACTION,
    
    // Безопасность и RNG
    SECURITY_ALERT,
    RNG_SEED_GENERATED,
    RNG_SEED_REVEALED
}
