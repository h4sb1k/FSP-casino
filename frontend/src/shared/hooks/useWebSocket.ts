import { useState, useEffect, useCallback } from 'react';
import { wsService } from '@shared/services';
import type { WsMessageType } from '@shared/types';

interface UseWebSocketOptions {
  autoConnect?: boolean;
  onConnect?: () => void;
  onDisconnect?: () => void;
  onError?: (error: Event) => void;
}

export function useWebSocket(options: UseWebSocketOptions = {}) {
  const {
    autoConnect = true,
    onConnect,
    onDisconnect,
    onError,
  } = options;

  const [isConnected, setIsConnected] = useState(wsService.getConnectionState());

  useEffect(() => {
    // Подписка на изменение состояния подключения
    const unsubscribeConnection = wsService.onConnectionChange((connected) => {
      setIsConnected(connected);
      if (connected && onConnect) {
        onConnect();
      } else if (!connected && onDisconnect) {
        onDisconnect();
      }
    });

    // Автоподключение
    if (autoConnect && !isConnected) {
      wsService.connect();
    }

    return () => {
      unsubscribeConnection();
    };
  }, [autoConnect, isConnected, onConnect, onDisconnect]);

  const send = useCallback(<T>(message: { type: WsMessageType; payload: T }) => {
    wsService.send(message);
  }, []);

  const subscribe = useCallback(<T>(
    type: WsMessageType | '*',
    handler: (payload: T) => void
  ) => {
    return wsService.subscribe(type, handler);
  }, []);

  const connect = useCallback(() => {
    wsService.connect();
  }, []);

  const disconnect = useCallback(() => {
    wsService.disconnect();
  }, []);

  return {
    isConnected,
    send,
    subscribe,
    connect,
    disconnect,
  };
}
