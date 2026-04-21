import axios, { AxiosInstance, AxiosRequestConfig, AxiosError } from 'axios';
import type { ApiError } from '@shared/types';

class ApiClient {
  private instance: AxiosInstance;

  constructor(baseURL: string = '/api') {
    this.instance = axios.create({
      baseURL,
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.setupInterceptors();
  }

  private setupInterceptors(): void {
    // Request interceptor - добавляем токен
    this.instance.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('accessToken');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // Response interceptor - обработка ошибок
    this.instance.interceptors.response.use(
      (response) => response,
      (error: AxiosError<ApiResponse<unknown>>) => {
        if (error.response?.status === 401) {
          // Токен истёк или невалиден
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
          window.location.href = '/login';
        }
        return Promise.reject(this.handleError(error));
      }
    );
  }

  private handleError(error: AxiosError): ApiError {
    if (error.response?.data) {
      const data = error.response.data as { error?: ApiError };
      return data.error || {
        code: 'UNKNOWN_ERROR',
        message: 'Произошла неизвестная ошибка',
      };
    }

    if (error.code === 'ECONNABORTED') {
      return {
        code: 'TIMEOUT',
        message: 'Превышено время ожидания ответа сервера',
      };
    }

    return {
      code: 'NETWORK_ERROR',
      message: 'Ошибка сети. Проверьте подключение к интернету.',
    };
  }

  async get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    type ApiResponse<T> = { success: boolean; data?: T; error?: ApiError };
    const response = await this.instance.get<ApiResponse<T>>(url, config);
    if (!response.data.success || !response.data.data) {
      throw new Error(response.data.error?.message || 'Ошибка запроса');
    }
    return response.data.data;
  }

  async post<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    type ApiResponse<T> = { success: boolean; data?: T; error?: ApiError };
    const response = await this.instance.post<ApiResponse<T>>(url, data, config);
    if (!response.data.success || !response.data.data) {
      throw new Error(response.data.error?.message || 'Ошибка запроса');
    }
    return response.data.data;
  }

  async put<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    type ApiResponse<T> = { success: boolean; data?: T; error?: ApiError };
    const response = await this.instance.put<ApiResponse<T>>(url, data, config);
    if (!response.data.success || !response.data.data) {
      throw new Error(response.data.error?.message || 'Ошибка запроса');
    }
    return response.data.data;
  }

  async patch<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    type ApiResponse<T> = { success: boolean; data?: T; error?: ApiError };
    const response = await this.instance.patch<ApiResponse<T>>(url, data, config);
    if (!response.data.success || !response.data.data) {
      throw new Error(response.data.error?.message || 'Ошибка запроса');
    }
    return response.data.data;
  }

  async delete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    type ApiResponse<T> = { success: boolean; data?: T; error?: ApiError };
    const response = await this.instance.delete<ApiResponse<T>>(url, config);
    if (!response.data.success) {
      throw new Error(response.data.error?.message || 'Ошибка запроса');
    }
    return response.data.data as T;
  }

  setAuthToken(token: string): void {
    if (token) {
      localStorage.setItem('accessToken', token);
    } else {
      localStorage.removeItem('accessToken');
    }
  }

  clearAuthToken(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  }
}

export const apiClient = new ApiClient();
export default apiClient;