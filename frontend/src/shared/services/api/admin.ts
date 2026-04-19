import apiClient from '../api';
import type {
  BotConfig,
  BotStats,
  PaginatedResponse,
  AuditLog,
  DashboardStats,
  AppSettings,
  EconomicRules,
} from '@shared/types';
import { API_ENDPOINTS } from '@shared/constants';

class AdminService {
  // Dashboard
  async getDashboardStats(): Promise<DashboardStats> {
    return apiClient.get<DashboardStats>(`${API_ENDPOINTS.ADMIN}/dashboard`);
  }

  // Bots
  async getBots(): Promise<BotConfig[]> {
    return apiClient.get<BotConfig[]>(`${API_ENDPOINTS.ADMIN}/bots`);
  }

  async createBot(config: Partial<BotConfig>): Promise<BotConfig> {
    return apiClient.post<BotConfig>(`${API_ENDPOINTS.ADMIN}/bots`, config);
  }

  async updateBot(id: string, config: Partial<BotConfig>): Promise<BotConfig> {
    return apiClient.put<BotConfig>(`${API_ENDPOINTS.ADMIN}/bots/${id}`, config);
  }

  async deleteBot(id: string): Promise<void> {
    return apiClient.delete(`${API_ENDPOINTS.ADMIN}/bots/${id}`);
  }

  async toggleBot(id: string, isActive: boolean): Promise<BotConfig> {
    return apiClient.patch<BotConfig>(`${API_ENDPOINTS.ADMIN}/bots/${id}/toggle`, { isActive });
  }

  async getBotStats(id: string): Promise<BotStats> {
    return apiClient.get<BotStats>(`${API_ENDPOINTS.ADMIN}/bots/${id}/stats`);
  }

  async getAllBotStats(): Promise<BotStats[]> {
    return apiClient.get<BotStats[]>(`${API_ENDPOINTS.ADMIN}/bots/stats`);
  }

  // Boosts (admin management)
  async createBoost(config: Partial<BoostConfig>): Promise<BoostConfig> {
    return apiClient.post<BoostConfig>(`${API_ENDPOINTS.ADMIN}/boosts`, config);
  }

  async updateBoost(id: string, config: Partial<BoostConfig>): Promise<BoostConfig> {
    return apiClient.put<BoostConfig>(`${API_ENDPOINTS.ADMIN}/boosts/${id}`, config);
  }

  async deleteBoost(id: string): Promise<void> {
    return apiClient.delete(`${API_ENDPOINTS.ADMIN}/boosts/${id}`);
  }

  async toggleBoost(id: string, isActive: boolean): Promise<BoostConfig> {
    return apiClient.patch<BoostConfig>(`${API_ENDPOINTS.ADMIN}/boosts/${id}/toggle`, { isActive });
  }

  // Economy settings
  async getEconomicRules(): Promise<EconomicRules> {
    return apiClient.get<EconomicRules>(`${API_ENDPOINTS.ADMIN}/economy/rules`);
  }

  async updateEconomicRules(rules: Partial<EconomicRules>): Promise<EconomicRules> {
    return apiClient.put<EconomicRules>(`${API_ENDPOINTS.ADMIN}/economy/rules`, rules);
  }

  async getAppSettings(): Promise<AppSettings> {
    return apiClient.get<AppSettings>(`${API_ENDPOINTS.ADMIN}/settings`);
  }

  async updateAppSettings(settings: Partial<AppSettings>): Promise<AppSettings> {
    return apiClient.put<AppSettings>(`${API_ENDPOINTS.ADMIN}/settings`, settings);
  }

  // Audit logs
  async getAuditLogs(
    filters?: {
      eventType?: string;
      userId?: string;
      roomId?: string;
      dateFrom?: string;
      dateTo?: string;
    },
    page = 1,
    pageSize = 20
  ): Promise<PaginatedResponse<AuditLog>> {
    const params = new URLSearchParams({ page: String(page), pageSize: String(pageSize) });
    if (filters) {
      Object.entries(filters).forEach(([key, value]) => {
        if (value !== undefined && value !== null) {
          params.append(key, String(value));
        }
      });
    }
    return apiClient.get<PaginatedResponse<AuditLog>>(`${API_ENDPOINTS.AUDIT}?${params.toString()}`);
  }

  async getAuditLog(id: string): Promise<AuditLog> {
    return apiClient.get<AuditLog>(`${API_ENDPOINTS.AUDIT}/${id}`);
  }

  // Users management
  async getUsers(page = 1, pageSize = 20): Promise<PaginatedResponse<unknown>> {
    return apiClient.get<PaginatedResponse<unknown>>(
      `${API_ENDPOINTS.ADMIN}/users?page=${page}&pageSize=${pageSize}`
    );
  }

  async updateUserRole(userId: string, role: string): Promise<unknown> {
    return apiClient.patch(`${API_ENDPOINTS.ADMIN}/users/${userId}/role`, { role });
  }

  async banUser(userId: string, reason: string): Promise<void> {
    return apiClient.post(`${API_ENDPOINTS.ADMIN}/users/${userId}/ban`, { reason });
  }

  async unbanUser(userId: string): Promise<void> {
    return apiClient.post(`${API_ENDPOINTS.ADMIN}/users/${userId}/unban`);
  }

  // Analytics
  async getAnalytics(period: 'day' | 'week' | 'month' | 'year'): Promise<unknown> {
    return apiClient.get(`${API_ENDPOINTS.ANALYTICS}?period=${period}`);
  }
}

// Helper type for BoostConfig (not yet imported)
interface BoostConfig {
  id: string;
  name: string;
  level: number;
  winChanceBonus: number;
  baseCost: number;
  costMultiplier: number;
  maxUsesPerRound: number;
  description: string;
  isActive: boolean;
}

export const adminService = new AdminService();
export default adminService;
