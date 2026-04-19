import apiClient from '../api';
import type {
  Transaction,
  TransactionFilters,
  PaginatedResponse,
  BonusOperation,
} from '@shared/types';
import { API_ENDPOINTS } from '@shared/constants';

class TransactionService {
  async getTransactions(
    filters?: TransactionFilters,
    page = 1,
    pageSize = 20
  ): Promise<PaginatedResponse<Transaction>> {
    const params = new URLSearchParams({ page: String(page), pageSize: String(pageSize) });
    if (filters) {
      Object.entries(filters).forEach(([key, value]) => {
        if (value !== undefined && value !== null) {
          params.append(key, String(value));
        }
      });
    }
    return apiClient.get<PaginatedResponse<Transaction>>(`${API_ENDPOINTS.TRANSACTIONS}?${params.toString()}`);
  }

  async getTransaction(id: string): Promise<Transaction> {
    return apiClient.get<Transaction>(`${API_ENDPOINTS.TRANSACTIONS}/${id}`);
  }

  async deposit(amount: number, method?: string): Promise<Transaction> {
    return apiClient.post<Transaction>(`${API_ENDPOINTS.TRANSACTIONS}/deposit`, {
      amount,
      method,
    });
  }

  async withdraw(amount: number, method?: string, details?: Record<string, unknown>): Promise<Transaction> {
    return apiClient.post<Transaction>(`${API_ENDPOINTS.TRANSACTIONS}/withdraw`, {
      amount,
      method,
      details,
    });
  }

  async getBonuses(): Promise<BonusOperation[]> {
    return apiClient.get<BonusOperation[]>(API_ENDPOINTS.BONUSES);
  }

  async applyPromoCode(code: string): Promise<{ bonus: BonusOperation; message: string }> {
    return apiClient.post<{ bonus: BonusOperation; message: string }>(
      `${API_ENDPOINTS.BONUSES}/promo`,
      { code }
    );
  }
}

export const transactionService = new TransactionService();
export default transactionService;
