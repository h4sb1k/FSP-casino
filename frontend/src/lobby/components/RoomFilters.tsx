/**
 * Фильтры для списка комнат
 */
import React from 'react';
import { RoomFilters } from '../../shared/types';
import './RoomFilters.css';

interface RoomFiltersProps {
  filters: RoomFilters;
  onFiltersChange: (filters: RoomFilters) => void;
}

const RoomFiltersComponent: React.FC<RoomFiltersProps> = ({ 
  filters, 
  onFiltersChange 
}) => {
  const handleMinBetChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value ? Number(e.target.value) : undefined;
    onFiltersChange({ ...filters, minEntryFee: value });
  };

  const handleMaxBetChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value ? Number(e.target.value) : undefined;
    onFiltersChange({ ...filters, maxEntryFee: value });
  };

  const handleMinPlayersChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value ? Number(e.target.value) : undefined;
    onFiltersChange({ ...filters, minPlayers: value });
  };

  const handleReset = () => {
    onFiltersChange({ status: 'WAITING' });
  };

  return (
    <div className="room-filters">
      <h4>Фильтры</h4>
      
      <div className="filter-group">
        <label htmlFor="minBet">Мин. ставка:</label>
        <input
          id="minBet"
          type="number"
          placeholder="0"
          value={filters.minEntryFee || ''}
          onChange={handleMinBetChange}
        />
      </div>

      <div className="filter-group">
        <label htmlFor="maxBet">Макс. ставка:</label>
        <input
          id="maxBet"
          type="number"
          placeholder="∞"
          value={filters.maxEntryFee || ''}
          onChange={handleMaxBetChange}
        />
      </div>

      <div className="filter-group">
        <label htmlFor="minPlayers">Мин. игроков:</label>
        <input
          id="minPlayers"
          type="number"
          placeholder="0"
          value={filters.minPlayers || ''}
          onChange={handleMinPlayersChange}
        />
      </div>

      <button className="btn btn-reset" onClick={handleReset}>
        Сбросить фильтры
      </button>
    </div>
  );
};

export default RoomFiltersComponent;
