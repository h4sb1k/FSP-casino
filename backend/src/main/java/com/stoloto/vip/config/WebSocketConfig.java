package com.stoloto.vip.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Включаем простой in-memory broker для каналов /topic и /queue
        config.enableSimpleBroker("/topic", "/queue");
        
        // Устанавливаем префикс для endpoint'ов приложения
        config.setApplicationDestinationPrefixes("/app");
        
        // Устанавливаем префикс для личных сообщений
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint для WebSocket подключений
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
        
        // Прямой WebSocket endpoint (без SockJS)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        // Оптимизация для большого количества подключений
        registration.setMessageSizeLimit(8192)
                .setSendBufferSizeLimit(512 * 1024)
                .setSendTimeLimit(20000);
    }
}
