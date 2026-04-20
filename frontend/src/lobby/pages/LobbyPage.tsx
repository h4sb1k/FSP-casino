/**
 * Страница лобби комнат - список доступных игровых комнат
 */
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Room, RoomType, RoomFilters } from '../../shared/types';
import { roomsApi } from '../../shared/services';
import { useAuth } from '../../shared/hooks';
import RoomList from '../components/RoomList';
import RoomFiltersComponent from '../components/RoomFilters';
import './LobbyPage.css';

const LobbyPage: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [rooms, setRooms] = useState<Room[]>([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState<RoomFilters>({
    status: 'WAITING'
  });
  const [selectedType, setSelectedType] = useState<RoomType | 'ALL'>('ALL');

  useEffect(() => {
    loadRooms();
    
    // Авто-обновление списка комнат каждые 5 секунд
    const interval = setInterval(loadRooms, 5000);
    return () => clearInterval(interval);
  }, [filters, selectedType]);

  const loadRooms = async () => {
    try {
      setLoading(true);
      const response = await roomsApi.getRooms({ 
        ...filters,
        type: selectedType === 'ALL' ? undefined : selectedType
      });
      setRooms(response.data || []);
    } catch (error) {
      console.error('Failed to load rooms:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleJoinRoom = async (roomId: string) => {
    try {
      await roomsApi.joinRoom(roomId);
      navigate(`/game/${roomId}`);
    } catch (error) {
      console.error('Failed to join room:', error);
      alert('Не удалось войти в комнату. Проверьте баланс.');
    }
  };

  const handleCreateRoom = async () => {
    // TODO: Открыть модалку создания комнаты
    alert('Функция создания комнаты будет доступна скоро');
  };

  if (!user) {
    navigate('/login');
    return null;
  }

  return (
    <div className="lobby-page">
      <div className="lobby-header">
        <h1>Игровые комнаты</h1>
        <button 
          className="btn btn-primary" 
          onClick={handleCreateRoom}
          disabled={user.balance <= 0}
        >
          Создать комнату
        </button>
      </div>

      {/* Фильтры по типам комнат */}
      <div className="room-type-tabs">
        <button
          className={`tab ${selectedType === 'ALL' ? 'active' : ''}`}
          onClick={() => setSelectedType('ALL')}
        >
          Все комнаты
        </button>
        <button
          className={`tab bronze ${selectedType === 'BRONZE' ? 'active' : ''}`}
          onClick={() => setSelectedType('BRONZE')}
        >
          🥉 Bronze
        </button>
        <button
          className={`tab gold ${selectedType === 'GOLD' ? 'active' : ''}`}
          onClick={() => setSelectedType('GOLD')}
        >
          🥇 Gold
        </button>
        <button
          className={`tab diamond ${selectedType === 'DIAMOND' ? 'active' : ''}`}
          onClick={() => setSelectedType('DIAMOND')}
        >
          💎 Diamond
        </button>
      </div>

      {/* Дополнительные фильтры */}
      <RoomFiltersComponent 
        filters={filters} 
        onFiltersChange={setFilters} 
      />

      {/* Список комнат */}
      {loading ? (
        <div className="loading">Загрузка комнат...</div>
      ) : rooms.length === 0 ? (
        <div className="empty-state">
          <p>Нет доступных комнат</p>
          <button className="btn btn-secondary" onClick={handleCreateRoom}>
            Создать первую комнату
          </button>
        </div>
      ) : (
        <RoomList rooms={rooms} onJoinRoom={handleJoinRoom} />
      )}
    </div>
  );
};

export default LobbyPage;
