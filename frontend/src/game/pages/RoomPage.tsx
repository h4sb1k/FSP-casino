/**
 * Страница игровой комнаты - основной интерфейс игры
 */
import React, { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Room, RoomPlayer, Round, BoostConfig } from '../../shared/types';
import { roomsApi, gameApi } from '../../shared/services';
import { useWebSocket } from '../../shared/hooks';
import { useAuth } from '../../shared/hooks';
import RoomTimer from '../components/RoomTimer';
import PlayersList from '../components/PlayersList';
import BetPanel from '../components/BetPanel';
import BoostPanel from '../components/BoostPanel';
import Notification from '../../shared/components/ui/Notification';
import './RoomPage.css';

const RoomPage: React.FC = () => {
  const { roomId } = useParams<{ roomId: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  
  const [room, setRoom] = useState<Room | null>(null);
  const [players, setPlayers] = useState<RoomPlayer[]>([]);
  const [currentRound, setCurrentRound] = useState<Round | null>(null);
  const [boosts, setBoosts] = useState<BoostConfig[]>([]);
  const [selectedBoost, setSelectedBoost] = useState<BoostConfig | null>(null);
  const [betAmount, setBetAmount] = useState<number>(0);
  const [hasJoined, setHasJoined] = useState(false);
  const [loading, setLoading] = useState(true);
  const [notification, setNotification] = useState<{message: string, type: 'success' | 'error' | 'info'} | null>(null);

  // WebSocket подключение
  const ws = useWebSocket();

  useEffect(() => {
    if (!roomId || !user) {
      navigate('/lobby');
      return;
    }

    loadRoomData();
    
    // Подписка на WebSocket события комнаты
    ws.subscribe(`room:${roomId}:state`, handleRoomUpdate);
    ws.subscribe(`room:${roomId}:round:start`, handleRoundStart);
    ws.subscribe(`room:${roomId}:round:end`, handleRoundEnd);
    ws.subscribe(`room:${roomId}:players`, handlePlayersUpdate);
    ws.subscribe(`user:${user.id}:balance`, handleBalanceUpdate);

    // Загрузка доступных бустов
    loadBoosts();

    return () => {
      ws.unsubscribe(`room:${roomId}:state`);
      ws.unsubscribe(`room:${roomId}:round:start`);
      ws.unsubscribe(`room:${roomId}:round:end`);
      ws.unsubscribe(`room:${roomId}:players`);
      ws.unsubscribe(`user:${user.id}:balance`);
    };
  }, [roomId, user]);

  const loadRoomData = async () => {
    try {
      setLoading(true);
      const response = await roomsApi.getRoom(roomId!);
      if (response.data) {
        setRoom(response.data);
        setPlayers(response.data.players || []);
        setBetAmount(response.data.minBet || 0);
      }
    } catch (error) {
      console.error('Failed to load room:', error);
      showNotification('Не удалось загрузить комнату', 'error');
      navigate('/lobby');
    } finally {
      setLoading(false);
    }
  };

  const loadBoosts = async () => {
    try {
      const response = await gameApi.getBoostConfigs();
      if (response.data) {
        setBoosts(response.data);
      }
    } catch (error) {
      console.error('Failed to load boosts:', error);
    }
  };

  const handleJoinRoom = async () => {
    if (!room) return;

    try {
      await roomsApi.joinRoom(roomId!, { betAmount });
      setHasJoined(true);
      showNotification('Вы успешно вошли в комнату', 'success');
      
      // Отправка события через WebSocket
      ws.send('JOIN_ROOM', { roomId, betAmount });
    } catch (error: any) {
      console.error('Failed to join room:', error);
      showNotification(error.response?.data?.message || 'Не удалось войти в комнату', 'error');
    }
  };

  const handleBuyBoost = async () => {
    if (!room || !selectedBoost) return;

    try {
      await gameApi.buyBoost(roomId!, selectedBoost.id);
      showNotification(`Буст "${selectedBoost.name}" успешно куплен! +${selectedBoost.winChanceBonus}% к победе`, 'success');
      ws.send('BUY_BOOST', { roomId, boostConfigId: selectedBoost.id });
      setSelectedBoost(null);
    } catch (error: any) {
      console.error('Failed to buy boost:', error);
      showNotification(error.response?.data?.message || 'Не удалось купить буст', 'error');
    }
  };

  const handleLeaveRoom = async () => {
    try {
      await roomsApi.leaveRoom(roomId!);
      ws.send('LEAVE_ROOM', { roomId });
      navigate('/lobby');
    } catch (error) {
      console.error('Failed to leave room:', error);
    }
  };

  // Обработчики WebSocket событий
  const handleRoomUpdate = (data: any) => {
    if (data.roomId === roomId) {
      setRoom(prev => prev ? { ...prev, ...data } : null);
    }
  };

  const handleRoundStart = (data: any) => {
    setCurrentRound(data);
    showNotification('Раунд начался!', 'info');
  };

  const handleRoundEnd = (data: any) => {
    const isWinner = data.winnerId === user?.id;
    if (isWinner) {
      showNotification(`🎉 Победа! Вы выиграли ${data.winningAmount} монет!`, 'success');
    } else {
      showNotification(`Раунд завершен. Победитель: ${data.winnerUsername}`, 'info');
    }
    
    // Автоматический возврат в лобби через 5 секунд
    setTimeout(() => navigate('/lobby'), 5000);
  };

  const handlePlayersUpdate = (data: RoomPlayer[]) => {
    setPlayers(data);
  };

  const handleBalanceUpdate = (data: any) => {
    // Обновление баланса пользователя в реальном времени
    showNotification(`Баланс обновлен: ${data.balance} монет`, 'info');
  };

  const showNotification = (message: string, type: 'success' | 'error' | 'info') => {
    setNotification({ message, type });
    setTimeout(() => setNotification(null), 4000);
  };

  if (loading) {
    return <div className="room-page-loading">Загрузка комнаты...</div>;
  }

  if (!room) {
    return null;
  }

  const isRoomFull = players.length >= room.maxPlayers;
  const canJoin = room.status === 'WAITING' && !isRoomFull && !hasJoined;

  return (
    <div className="room-page">
      {notification && (
        <Notification 
          message={notification.message} 
          type={notification.type} 
          onClose={() => setNotification(null)} 
        />
      )}

      {/* Header с таймером */}
      <div className="room-header">
        <h1>{room.name}</h1>
        <div className="room-meta">
          <span className={`room-type ${room.type.toLowerCase()}`}>
            {room.type === 'BRONZE' ? '🥉' : room.type === 'GOLD' ? '🥇' : '💎'} {room.type}
          </span>
          <span className={`room-status status-${room.status.toLowerCase()}`}>
            {room.status === 'WAITING' ? 'Ожидание игроков' : 'Игра идет'}
          </span>
        </div>
        {currentRound && (
          <RoomTimer 
            startTime={currentRound.startTime} 
            endTime={currentRound.endTime} 
          />
        )}
      </div>

      {/* Основной контент */}
      <div className="room-content">
        {/* Левая колонка - Список игроков */}
        <div className="room-sidebar">
          <PlayersList players={players} currentUserId={user?.id} />
        </div>

        {/* Центральная колонка - Игровая зона */}
        <div className="room-game-area">
          {!hasJoined ? (
            <div className="join-panel">
              <h2>Вход в комнату</h2>
              <p>Минимальная ставка: {room.minBet} монет</p>
              <p>Максимальная ставка: {room.maxBet} монет</p>
              
              <div className="bet-input-group">
                <label htmlFor="betAmount">Ваша ставка:</label>
                <input
                  id="betAmount"
                  type="number"
                  min={room.minBet}
                  max={room.maxBet}
                  value={betAmount}
                  onChange={(e) => setBetAmount(Number(e.target.value))}
                />
              </div>

              <button
                className="btn btn-join"
                onClick={handleJoinRoom}
                disabled={!canJoin || betAmount < room.minBet}
              >
                {isRoomFull ? 'Комната заполнена' : 'Войти и сделать ставку'}
              </button>
            </div>
          ) : (
            <div className="game-placeholder">
              <h2>Игровая зона</h2>
              <p>Здесь будет отображаться игра от партнера</p>
              <div className="game-status">
                {room.status === 'WAITING' ? (
                  <p>⏳ Ожидание начала раунда...</p>
                ) : (
                  <p>🎮 Игра активна!</p>
                )}
              </div>
            </div>
          )}
        </div>

        {/* Правая колонка - Панель управления */}
        <div className="room-controls">
          {hasJoined && room.status === 'WAITING' && (
            <>
              <BoostPanel
                boosts={boosts}
                selectedBoost={selectedBoost}
                onSelectBoost={setSelectedBoost}
                onBuyBoost={handleBuyBoost}
                userBonusBalance={user?.bonusBalance || 0}
              />
              
              <button
                className="btn btn-leave"
                onClick={handleLeaveRoom}
              >
                Выйти из комнаты
              </button>
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default RoomPage;
