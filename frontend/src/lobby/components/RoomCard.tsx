/**
 * Карточка игровой комнаты
 */
import React from 'react';
import { Room, RoomType } from '../../shared/types';
import './RoomCard.css';

interface RoomCardProps {
  room: Room;
  onJoin: () => void;
}

const getRoomTypeIcon = (type: RoomType): string => {
  switch (type) {
    case 'BRONZE': return '🥉';
    case 'GOLD': return '🥇';
    case 'DIAMOND': return '💎';
    default: return '🏠';
  }
};

const getRoomTypeClass = (type: RoomType): string => {
  switch (type) {
    case 'BRONZE': return 'bronze';
    case 'GOLD': return 'gold';
    case 'DIAMOND': return 'diamond';
    default: return '';
  }
};

const RoomCard: React.FC<RoomCardProps> = ({ room, onJoin }) => {
  const occupancyPercent = (room.currentPlayers / room.maxPlayers) * 100;
  const isFull = room.currentPlayers >= room.maxPlayers;
  const isWaiting = room.status === 'WAITING';

  return (
    <div className={`room-card ${getRoomTypeClass(room.type)}`}>
      <div className="room-card-header">
        <span className="room-type-icon">{getRoomTypeIcon(room.type)}</span>
        <h3 className="room-name">{room.name}</h3>
        <span className={`room-status status-${room.status.toLowerCase()}`}>
          {room.status === 'WAITING' ? 'Ожидание' : room.status === 'ACTIVE' ? 'Игра идет' : 'Завершена'}
        </span>
      </div>

      <div className="room-card-body">
        <div className="room-info-row">
          <span className="label">Вход:</span>
          <span className="value">{room.entryFee} монет</span>
        </div>

        <div className="room-info-row">
          <span className="label">Ставки:</span>
          <span className="value">{room.minBet} - {room.maxBet}</span>
        </div>

        <div className="room-info-row">
          <span className="label">Призовой фонд:</span>
          <span className="value prize">{room.prizePool} монет</span>
        </div>

        <div className="room-players">
          <div className="players-count">
            <span className="label">Игроки:</span>
            <span className="value">{room.currentPlayers}/{room.maxPlayers}</span>
          </div>
          <div className="progress-bar">
            <div 
              className="progress-fill" 
              style={{ width: `${occupancyPercent}%` }}
            />
          </div>
          {room.botCount > 0 && (
            <div className="bot-indicator">
              🤖 {room.botCount} бот(ов)
            </div>
          )}
        </div>
      </div>

      <div className="room-card-footer">
        <button
          className="btn btn-join"
          onClick={onJoin}
          disabled={!isWaiting || isFull}
        >
          {isFull ? 'Комната заполнена' : !isWaiting ? 'Игра идет' : 'Войти в комнату'}
        </button>
      </div>
    </div>
  );
};

export default RoomCard;
