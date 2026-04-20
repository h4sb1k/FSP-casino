package com.stoloto.vip.domain.enums;

/**
 * Тип актора в аудите
 */
public enum AuditActorType {
    USER,     // Обычный пользователь
    BOT,      // Системный бот
    SYSTEM,   // Системное событие
    ADMIN     // Действие администратора
}
