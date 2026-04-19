package com.stoloto.vip.core.enums;

public enum TransactionType {
    DEPOSIT,          // Пополнение баланса
    WITHDRAWAL,       // Вывод средств
    BET_PLACE,        // Размещение ставки
    BET_WIN,          // Выигрыш
    BET_LOSS,         // Проигрыш
    BONUS_ACCRUAL,    // Начисление бонуса
    BONUS_SPEND,      // Списание бонуса (покупка буста)
    REFUND,           // Возврат средств
    SYSTEM_ADJUSTMENT // Корректировка системой
}
