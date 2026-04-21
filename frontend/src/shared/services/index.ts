/**
 * API Services - модуль экспорта всех сервисов
 */

export { default as apiClient } from './api';
export { default as authService } from './api/auth';
export { default as roomService } from './api/rooms';
export { default as gameService } from './api/game';
export { default as transactionService } from './api/transactions';
export { default as adminService } from './api/admin';
export { default as wsService, createWebSocketService } from './websocket';
