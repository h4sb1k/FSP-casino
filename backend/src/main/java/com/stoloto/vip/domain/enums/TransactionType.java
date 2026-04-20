package com.stoloto.vip.domain.enums;

/**
 * Типы финансовых транзакций
 */
public enum TransactionType {
    DEPOSIT,                    // Пополнение счета
    WITHDRAWAL,                 // Вывод средств
    BET_PLACE,                  // Размещение ставки (холдирование)
    BET_HOLD,                   // Холдирование средств
    BET_RELEASE,                // Возврат холдированных средств
    BET_WIN,                    // Выплата выигрыша
    BOOST_PURCHASE,             // Покупка буста за бонусы
    BONUS_GRANT,                // Начисление бонусов
    BOT_WIN_CASINO_TRANSFER     // Перевод выигрыша бота в казино
}
