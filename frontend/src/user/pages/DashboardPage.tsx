/**
 * Страница дашборда пользователя
 */
import React, { useState, useEffect } from 'react';
import { useAuth } from '../../shared/hooks';
import { userApi } from '../../shared/services';
import { User, UserStats } from '../../shared/types';
import './DashboardPage.css';

const DashboardPage: React.FC = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState<UserStats | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    try {
      const response = await userApi.getStats();
      if (response.data) {
        setStats(response.data);
      }
    } catch (error) {
      console.error('Failed to load stats:', error);
    } finally {
      setLoading(false);
    }
  };

  if (!user) return null;

  return (
    <div className="dashboard-page">
      <h1>Личный кабинет</h1>
      
      <div className="dashboard-header">
        <div className="welcome-card">
          <h2>Добро пожаловать, {user.username}!</h2>
          <div className="balance-overview">
            <div className="balance-item">
              <span className="label">Основной баланс:</span>
              <span className="value">{user.balance} монет</span>
            </div>
            <div className="balance-item bonus">
              <span className="label">Бонусные баллы:</span>
              <span className="value">{user.bonusBalance} ⭐</span>
            </div>
          </div>
        </div>
      </div>

      <div className="dashboard-stats">
        <h3>Ваша статистика</h3>
        
        {loading ? (
          <p>Загрузка статистики...</p>
        ) : stats ? (
          <div className="stats-grid">
            <div className="stat-card">
              <div className="stat-value">{stats.totalGames}</div>
              <div className="stat-label">Игр сыграно</div>
            </div>
            <div className="stat-card">
              <div className="stat-value">{stats.totalWins}</div>
              <div className="stat-label">Побед</div>
            </div>
            <div className="stat-card">
              <div className="stat-value">{stats.winRate.toFixed(1)}%</div>
              <div className="stat-label">Процент побед</div>
            </div>
            <div className="stat-card">
              <div className="stat-value">{stats.totalWon}</div>
              <div className="stat-label">Всего выиграно</div>
            </div>
            <div className="stat-card">
              <div className="stat-value">{stats.biggestWin}</div>
              <div className="stat-label">Крупнейший выигрыш</div>
            </div>
            <div className="stat-card">
              <div className={`stat-value ${stats.netProfit >= 0 ? 'positive' : 'negative'}`}>
                {stats.netProfit >= 0 ? '+' : ''}{stats.netProfit}
              </div>
              <div className="stat-label">Чистая прибыль</div>
            </div>
          </div>
        ) : (
          <p>Пока нет статистики игр</p>
        )}
      </div>

      <div className="dashboard-actions">
        <h3>Быстрые действия</h3>
        <div className="actions-grid">
          <a href="/lobby" className="action-card">
            <span className="action-icon">🎮</span>
            <span className="action-label">Играть сейчас</span>
          </a>
          <a href="/profile/history" className="action-card">
            <span className="action-icon">📜</span>
            <span className="action-label">История игр</span>
          </a>
          <a href="/profile/transactions" className="action-card">
            <span className="action-icon">💰</span>
            <span className="action-label">Транзакции</span>
          </a>
          <a href="/profile" className="action-card">
            <span className="action-icon">⚙️</span>
            <span className="action-label">Настройки</span>
          </a>
        </div>
      </div>
    </div>
  );
};

export default DashboardPage;
