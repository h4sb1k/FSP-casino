import apiClient from '../api';
import type {
  User,
  AuthTokens,
  LoginRequest,
  RegisterRequest,
} from '@shared/types';
import { API_ENDPOINTS } from '@shared/constants';

export interface RefreshTokenResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}

class AuthService {
  async login(credentials: LoginRequest): Promise<{ user: User; tokens: AuthTokens }> {
    return apiClient.post<{ user: User; tokens: AuthTokens }>(
      `${API_ENDPOINTS.AUTH}/login`,
      credentials
    );
  }

  async register(data: RegisterRequest): Promise<{ user: User; tokens: AuthTokens }> {
    return apiClient.post<{ user: User; tokens: AuthTokens }>(
      `${API_ENDPOINTS.AUTH}/register`,
      data
    );
  }

  async logout(): Promise<void> {
    return apiClient.post(`${API_ENDPOINTS.AUTH}/logout`);
  }

  async refreshToken(refreshToken: string): Promise<RefreshTokenResponse> {
    return apiClient.post<RefreshTokenResponse>(`${API_ENDPOINTS.AUTH}/refresh`, {
      refreshToken,
    });
  }

  async getCurrentUser(): Promise<User> {
    return apiClient.get<User>(`${API_ENDPOINTS.AUTH}/me`);
  }

  async updateProfile(data: Partial<User>): Promise<User> {
    return apiClient.put<User>(`${API_ENDPOINTS.USERS}/profile`, data);
  }

  async changePassword(oldPassword: string, newPassword: string): Promise<void> {
    return apiClient.post(`${API_ENDPOINTS.USERS}/change-password`, {
      oldPassword,
      newPassword,
    });
  }

  setAuthTokens(tokens: AuthTokens): void {
    localStorage.setItem('accessToken', tokens.accessToken);
    localStorage.setItem('refreshToken', tokens.refreshToken);
    apiClient.setAuthToken(tokens.accessToken);
  }

  getAccessToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  getRefreshToken(): string | null {
    return localStorage.getItem('refreshToken');
  }

  clearAuthTokens(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    apiClient.removeAuthToken();
  }

  isAuthenticated(): boolean {
    return !!this.getAccessToken();
  }
}

export const authService = new AuthService();
export default authService;
