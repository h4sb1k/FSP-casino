/**
 * Заглушки для страниц пользователя
 */
import React from 'react';

export const ProfilePage: React.FC = () => (
  <div className="placeholder-page">
    <h1>Профиль</h1>
    <p>Страница настроек профиля в разработке</p>
  </div>
);

export const TransactionsPage: React.FC = () => (
  <div className="placeholder-page">
    <h1>Транзакции</h1>
    <p>История транзакций в разработке</p>
  </div>
);

export const HistoryPage: React.FC = () => (
  <div className="placeholder-page">
    <h1>История игр</h1>
    <p>История игр в разработке</p>
  </div>
);
