import apiClient from '../api';
import type {
  Room,
  RoomConfig,
  RoomFilters,
  RoomPlayer,
  PaginatedResponse,
  RoomStats,
} from '@shared/types';
import { API_ENDPOINTS } from '@shared/constants';

class RoomService {
  async getRooms(filters?: RoomFilters): Promise<PaginatedResponse<Room>> {
    const params = new URLSearchParams();
    if (filters) {
      Object.entries(filters).forEach(([key, value]) => {
        if (value !== undefined && value !== null) {
          params.append(key, String(value));
        }
      });
    }
    return apiClient.get<PaginatedResponse<Room>>(`${API_ENDPOINTS.ROOMS}?${params.toString()}`);
  }

  async getRoom(id: string): Promise<Room> {
    return apiClient.get<Room>(`${API_ENDPOINTS.ROOMS}/${id}`);
  }

  async createRoom(configId: string): Promise<Room> {
    return apiClient.post<Room>(API_ENDPOINTS.ROOMS, { configId });
  }

  async joinRoom(roomId: string): Promise<{ room: Room; player: RoomPlayer }> {
    return apiClient.post<{ room: Room; player: RoomPlayer }>(
      `${API_ENDPOINTS.ROOMS}/${roomId}/join`
    );
  }

  async leaveRoom(roomId: string): Promise<void> {
    return apiClient.post(`${API_ENDPOINTS.ROOMS}/${roomId}/leave`);
  }

  async getPlayers(roomId: string): Promise<RoomPlayer[]> {
    return apiClient.get<RoomPlayer[]>(`${API_ENDPOINTS.ROOMS}/${roomId}/players`);
  }

  async getRoomStats(): Promise<RoomStats> {
    return apiClient.get<RoomStats>(`${API_ENDPOINTS.ROOMS}/stats`);
  }

  // Admin methods
  async getRoomConfigs(): Promise<RoomConfig[]> {
    return apiClient.get<RoomConfig[]>(`${API_ENDPOINTS.ADMIN}/room-configs`);
  }

  async createRoomConfig(config: Partial<RoomConfig>): Promise<RoomConfig> {
    return apiClient.post<RoomConfig>(`${API_ENDPOINTS.ADMIN}/room-configs`, config);
  }

  async updateRoomConfig(id: string, config: Partial<RoomConfig>): Promise<RoomConfig> {
    return apiClient.put<RoomConfig>(`${API_ENDPOINTS.ADMIN}/room-configs/${id}`, config);
  }

  async deleteRoomConfig(id: string): Promise<void> {
    return apiClient.delete(`${API_ENDPOINTS.ADMIN}/room-configs/${id}`);
  }

  async toggleRoomConfig(id: string, isActive: boolean): Promise<RoomConfig> {
    return apiClient.patch<RoomConfig>(`${API_ENDPOINTS.ADMIN}/room-configs/${id}/toggle`, {
      isActive,
    });
  }
}

export const roomService = new RoomService();
export default roomService;
