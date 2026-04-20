/**
 * Панель выбора и покупки бустов
 */
import React, { useState } from 'react';
import { BoostConfig } from '../../shared/types';
import './BoostPanel.css';

interface BoostPanelProps {
  boosts: BoostConfig[];
  selectedBoost: BoostConfig | null;
  onSelectBoost: (boost: BoostConfig | null) => void;
  onBuyBoost: () => void;
  userBonusBalance: number;
}

const BoostPanel: React.FC<BoostPanelProps> = ({
  boosts,
  selectedBoost,
  onSelectBoost,
  onBuyBoost,
  userBonusBalance
}) => {
  const [isPurchasing, setIsPurchasing] = useState(false);

  const handleSelectBoost = (boost: BoostConfig) => {
    if (selectedBoost?.id === boost.id) {
      onSelectBoost(null);
    } else {
      onSelectBoost(boost);
    }
  };

  const handleBuyClick = () => {
    if (!selectedBoost) return;
    
    if (userBonusBalance < selectedBoost.baseCost) {
      alert('Недостаточно бонусных баллов');
      return;
    }

    setIsPurchasing(true);
    onBuyBoost();
    setTimeout(() => setIsPurchasing(false), 1000);
  };

  const canAfford = (boost: BoostConfig): boolean => {
    return userBonusBalance >= boost.baseCost;
  };

  if (!boosts || boosts.length === 0) {
    return (
      <div className="boost-panel">
        <p>Бусты недоступны в этой комнате</p>
      </div>
    );
  }

  return (
    <div className="boost-panel">
      <h3 className="boost-panel-title">⚡ Бусты к победе</h3>
      <p className="boost-panel-subtitle">
        Увеличьте свои шансы на победу!
      </p>

      <div className="boost-list">
        {boosts.map(boost => (
          <div
            key={boost.id}
            className={`boost-item ${selectedBoost?.id === boost.id ? 'selected' : ''} ${!canAfford(boost) ? 'disabled' : ''}`}
            onClick={() => handleSelectBoost(boost)}
          >
            <div className="boost-header">
              <span className="boost-name">{boost.name}</span>
              <span className="boost-level">Уровень {boost.level}</span>
            </div>
            
            <div className="boost-description">
              {boost.description}
            </div>
            
            <div className="boost-stats">
              <div className="boost-stat">
                <span className="label">Шанс победы:</span>
                <span className="value success">+{boost.winChanceBonus}%</span>
              </div>
              
              <div className="boost-stat">
                <span className="label">Стоимость:</span>
                <span className={`value ${canAfford(boost) ? 'warning' : 'error'}`}>
                  {boost.baseCost} бонусов
                </span>
              </div>
            </div>
          </div>
        ))}
      </div>

      {selectedBoost && (
        <div className="boost-purchase-section">
          <div className="your-bonus-balance">
            Ваш баланс бонусов: <strong>{userBonusBalance}</strong>
          </div>
          
          <button
            className="btn btn-buy-boost"
            onClick={handleBuyClick}
            disabled={!canAfford(selectedBoost) || isPurchasing}
          >
            {isPurchasing ? 'Покупка...' : `Купить за ${selectedBoost.baseCost} бонусов`}
          </button>
          
          <button
            className="btn btn-cancel"
            onClick={() => onSelectBoost(null)}
          >
            Отмена
          </button>
        </div>
      )}

      <div className="boost-info">
        <p>ℹ️ Буст действует только на текущий раунд</p>
        <p>ℹ️ Можно использовать только 1 буст за игру</p>
      </div>
    </div>
  );
};

export default BoostPanel;
