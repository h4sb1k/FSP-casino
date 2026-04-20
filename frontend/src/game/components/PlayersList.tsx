/**
 * Список игроков в комнате
 */
import React from 'react';
import { RoomPlayer } from '../../shared/types';
import './PlayersList.css';

interface PlayersListProps {
  players: RoomPlayer[];
  currentUserId?: string;
}

const PlayersList: React.FC<PlayersListProps> = ({ players, currentUserId }) => {
  if (!players || players.length === 0) {
    return (
      <div className="players-list-empty">
        <p>Нет игроков в комнате</p>
      </div>
    );
  }

  return (
    <div className="players-list">
      <h3 className="players-list-title">
        Игроки ({players.length})
      </h3>
      
      <div className="players-list-content">
        {players.map((player, index) => {
          const isCurrentUser = player.userId === currentUserId;
          
          return (
            <div 
              key={player.id} 
              className={`player-row ${isCurrentUser ? 'current-user' : ''}`}
            >
              <div className="player-index">{index + 1}</div>
              
              <div className="player-avatar">
                {player.isBot ? '🤖' : '👤'}
              </div>
              
              <div className="player-info">
                <div className="player-name">
                  {player.username}
                  {isCurrentUser && <span className="you-badge">(Вы)</span>}
                  {player.isBot && <span className="bot-badge">Бот</span>}
                </div>
                
                {player.betAmount !== undefined && (
                  <div className="player-bet">
                    Ставка: {player.betAmount} монет
                  </div>
                )}
                
                {player.hasBoost && (
                  <div className="player-boost">
                    ⚡ Буст активен (+{player.boostLevel}%)
                  </div>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default PlayersList;
