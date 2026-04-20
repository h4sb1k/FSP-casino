/**
 * Список игровых комнат с карточками
 */
import React from 'react';
import { Room } from '../../shared/types';
import RoomCard from './RoomCard';
import './RoomList.css';

interface RoomListProps {
  rooms: Room[];
  onJoinRoom: (roomId: string) => void;
}

const RoomList: React.FC<RoomListProps> = ({ rooms, onJoinRoom }) => {
  if (!rooms || rooms.length === 0) {
    return null;
  }

  return (
    <div className="room-list">
      {rooms.map(room => (
        <RoomCard 
          key={room.id} 
          room={room} 
          onJoin={() => onJoinRoom(room.id)} 
        />
      ))}
    </div>
  );
};

export default RoomList;
