/**
 * API Services - модуль экспорта всех сервисов
 */

export { default as apiClient } from './api';
export { default as authService, authService } from './api/auth';
export { default as roomService, roomService } from './api/rooms';
export { default as gameService, gameService } from './api/game';
export { default as transactionService, transactionService } from './api/transactions';
export { default as adminService, adminService } from './api/admin';
export { default as wsService, wsService, createWebSocketService } from './websocket';
