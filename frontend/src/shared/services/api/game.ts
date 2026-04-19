import apiClient from '../api';
import type {
  Bet,
  BoostConfig,
  BoostUsage,
  Round,
} from '@shared/types';
import { API_ENDPOINTS } from '@shared/constants';

class GameService {
  // Rounds
  async getCurrentRound(roomId: string): Promise<Round> {
    return apiClient.get<Round>(`${API_ENDPOINTS.ROOMS}/${roomId}/round/current`);
  }

  async getRoundHistory(roomId: string, page = 1, pageSize = 20): Promise<{ items: Round[]; total: number }> {
    return apiClient.get<{ items: Round[]; total: number }>(
      `${API_ENDPOINTS.ROOMS}/${roomId}/rounds?page=${page}&pageSize=${pageSize}`
    );
  }

  // Bets
  async placeBet(roomId: string, amount: number, useBoost = false, boostLevel = 0): Promise<Bet> {
    return apiClient.post<Bet>(`${API_ENDPOINTS.ROOMS}/${roomId}/bets`, {
      amount,
      useBoost,
      boostLevel,
    });
  }

  async getBet(betId: string): Promise<Bet> {
    return apiClient.get<Bet>(`${API_ENDPOINTS.BETS}/${betId}`);
  }

  async getUserBets(roomId?: string, page = 1, pageSize = 20): Promise<{ items: Bet[]; total: number }> {
    const params = new URLSearchParams({ page: String(page), pageSize: String(pageSize) });
    if (roomId) {
      params.append('roomId', roomId);
    }
    return apiClient.get<{ items: Bet[]; total: number }>(`${API_ENDPOINTS.BETS}?${params.toString()}`);
  }

  // Boosts
  async getBoostConfigs(): Promise<BoostConfig[]> {
    return apiClient.get<BoostConfig[]>(API_ENDPOINTS.BOOSTS);
  }

  async activateBoost(roomId: string, level: number): Promise<{ boost: BoostUsage; transaction: unknown }> {
    return apiClient.post<{ boost: BoostUsage; transaction: unknown }>(
      `${API_ENDPOINTS.ROOMS}/${roomId}/boost`,
      { level }
    );
  }

  async getBoostUsage(roomId: string): Promise<BoostUsage[]> {
    return apiClient.get<BoostUsage[]>(`${API_ENDPOINTS.ROOMS}/${roomId}/boosts/usage`);
  }

  // Game results
  async getRoundResults(roundId: string): Promise<{ round: Round; bets: Bet[] }> {
    return apiClient.get<{ round: Round; bets: Bet[] }>(`${API_ENDPOINTS.ROUNDS}/${roundId}/results`);
  }
}

export const gameService = new GameService();
export default gameService;
