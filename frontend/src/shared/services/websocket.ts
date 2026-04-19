import type {
  WsMessage,
  WsMessageType,
  RoomUpdatePayload,
  RoundStartPayload,
  RoundResultPayload,
  BalanceUpdatePayload,
} from '@shared/types';
import { WS_MESSAGE_TYPES, LIMITS } from '@shared/constants';

type MessageHandler<T = unknown> = (payload: T) => void;

interface WebSocketConfig {
  url: string;
  reconnectDelay?: number;
  maxReconnectAttempts?: number;
  pingInterval?: number;
}

class WebSocketService {
  private socket: WebSocket | null = null;
  private config: WebSocketConfig;
  private reconnectAttempts = 0;
  private pingIntervalId: ReturnType<typeof setInterval> | null = null;
  private messageHandlers = new Map<string, Set<MessageHandler>>();
  private isConnected = false;
  private isManualClose = false;

  constructor(config: WebSocketConfig) {
    this.config = config;
  }

  connect(): void {
    if (this.socket?.readyState === WebSocket.OPEN) {
      console.log('[WS] Already connected');
      return;
    }

    this.isManualClose = false;
    this.socket = new WebSocket(this.config.url);
    this.setupEventListeners();
  }

  private setupEventListeners(): void {
    if (!this.socket) return;

    this.socket.onopen = () => {
      console.log('[WS] Connected');
      this.isConnected = true;
      this.reconnectAttempts = 0;
      this.startPingInterval();
      this.notifyConnectionChange(true);
    };

    this.socket.onclose = (event) => {
      console.log('[WS] Disconnected', event.code, event.reason);
      this.isConnected = false;
      this.stopPingInterval();
      this.notifyConnectionChange(false);

      if (!this.isManualClose) {
        this.attemptReconnect();
      }
    };

    this.socket.onerror = (error) => {
      console.error('[WS] Error:', error);
    };

    this.socket.onmessage = (event) => {
      try {
        const message: WsMessage = JSON.parse(event.data);
        this.handleMessage(message);
      } catch (error) {
        console.error('[WS] Failed to parse message:', error);
      }
    };
  }

  private handleMessage(message: WsMessage): void {
    console.log('[WS] Message received:', message.type);

    // Обработка PONG
    if (message.type === WS_MESSAGE_TYPES.PONG) {
      return;
    }

    // Вызов обработчиков для конкретного типа сообщения
    const handlers = this.messageHandlers.get(message.type);
    if (handlers) {
      handlers.forEach((handler) => {
        try {
          handler(message.payload);
        } catch (error) {
          console.error(`[WS] Error in handler for ${message.type}:`, error);
        }
      });
    }

    // Глобальные обработчики
    const globalHandlers = this.messageHandlers.get('*');
    if (globalHandlers) {
      globalHandlers.forEach((handler) => {
        try {
          handler(message);
        } catch (error) {
          console.error('[WS] Error in global handler:', error);
        }
      });
    }
  }

  private attemptReconnect(): void {
    const maxAttempts = this.config.maxReconnectAttempts || LIMITS.MAX_RECONNECT_ATTEMPTS;

    if (this.reconnectAttempts >= maxAttempts) {
      console.error('[WS] Max reconnect attempts reached');
      return;
    }

    this.reconnectAttempts++;
    const delay = this.config.reconnectDelay || LIMITS.RECONNECT_DELAY_MS;

    console.log(`[WS] Reconnecting in ${delay}ms (attempt ${this.reconnectAttempts}/${maxAttempts})`);

    setTimeout(() => {
      if (!this.isManualClose) {
        this.connect();
      }
    }, delay);
  }

  private startPingInterval(): void {
    const interval = this.config.pingInterval || LIMITS.PING_INTERVAL_MS;

    this.pingIntervalId = setInterval(() => {
      if (this.socket?.readyState === WebSocket.OPEN) {
        this.send({ type: WS_MESSAGE_TYPES.PING, payload: {} });
      }
    }, interval);
  }

  private stopPingInterval(): void {
    if (this.pingIntervalId) {
      clearInterval(this.pingIntervalId);
      this.pingIntervalId = null;
    }
  }

  send<T>(message: Omit<WsMessage<T>, 'timestamp'>): void {
    if (!this.socket || this.socket.readyState !== WebSocket.OPEN) {
      console.warn('[WS] Cannot send message - not connected');
      return;
    }

    const fullMessage: WsMessage<T> = {
      ...message,
      timestamp: new Date().toISOString(),
    };

    try {
      this.socket.send(JSON.stringify(fullMessage));
    } catch (error) {
      console.error('[WS] Failed to send message:', error);
    }
  }

  subscribe<T>(type: WsMessageType | '*', handler: MessageHandler<T>): () => void {
    if (!this.messageHandlers.has(type)) {
      this.messageHandlers.set(type, new Set());
    }

    const handlers = this.messageHandlers.get(type)!;
    handlers.add(handler as MessageHandler);

    // Возвращаем функцию отписки
    return () => {
      handlers.delete(handler as MessageHandler);
      if (handlers.size === 0) {
        this.messageHandlers.delete(type);
      }
    };
  }

  unsubscribeAll(): void {
    this.messageHandlers.clear();
  }

  disconnect(): void {
    this.isManualClose = true;
    this.stopPingInterval();

    if (this.socket) {
      this.socket.close(1000, 'Manual disconnect');
      this.socket = null;
    }

    this.isConnected = false;
  }

  getConnectionState(): boolean {
    return this.isConnected;
  }

  // Специализированные методы для типов сообщений
  onRoomUpdate(handler: MessageHandler<RoomUpdatePayload>): () => void {
    return this.subscribe(WS_MESSAGE_TYPES.ROOM_UPDATE, handler);
  }

  onRoundStart(handler: MessageHandler<RoundStartPayload>): () => void {
    return this.subscribe(WS_MESSAGE_TYPES.ROUND_START, handler);
  }

  onRoundResult(handler: MessageHandler<RoundResultPayload>): () => void {
    return this.subscribe(WS_MESSAGE_TYPES.ROUND_RESULT, handler);
  }

  onBalanceUpdate(handler: MessageHandler<BalanceUpdatePayload>): () => void {
    return this.subscribe(WS_MESSAGE_TYPES.BALANCE_UPDATE, handler);
  }

  private notifyConnectionChange(connected: boolean): void {
    const handlers = this.messageHandlers.get('CONNECTION_CHANGE');
    if (handlers) {
      handlers.forEach((handler) => handler(connected));
    }
  }

  onConnectionChange(handler: MessageHandler<boolean>): () => void {
    if (!this.messageHandlers.has('CONNECTION_CHANGE')) {
      this.messageHandlers.set('CONNECTION_CHANGE', new Set());
    }
    const handlers = this.messageHandlers.get('CONNECTION_CHANGE')!;
    handlers.add(handler as MessageHandler);

    return () => {
      handlers.delete(handler as MessageHandler);
      if (handlers.size === 0) {
        this.messageHandlers.delete('CONNECTION_CHANGE');
      }
    };
  }
}

// Factory function для создания экземпляра с URL из env
export function createWebSocketService(): WebSocketService {
  const wsUrl = import.meta.env.VITE_WS_URL || `ws://${window.location.host}/ws`;
  return new WebSocketService({
    url: wsUrl,
    reconnectDelay: LIMITS.RECONNECT_DELAY_MS,
    maxReconnectAttempts: LIMITS.MAX_RECONNECT_ATTEMPTS,
    pingInterval: LIMITS.PING_INTERVAL_MS,
  });
}

// Singleton instance
export const wsService = createWebSocketService();
export default wsService;
