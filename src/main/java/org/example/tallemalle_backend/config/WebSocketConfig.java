package org.example.tallemalle_backend.config;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.config.interceptor.JwtHandshakeInterceptor;
import org.example.tallemalle_backend.config.interceptor.StompSubscriptionInterceptor;
import org.example.tallemalle_backend.config.websocket.WebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final WebSocketHandler webSocketHandler;
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;
    private final StompSubscriptionInterceptor stompSubscriptionInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")

                .addInterceptors(jwtHandshakeInterceptor)
                .setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); // 구독자가 메시지를 받을 경로의 시작 부분
        registry.setApplicationDestinationPrefixes("/app"); // 클라이언트가 메시지를 보낼 때 사용할 주소의 시작 부분
        registry.setUserDestinationPrefix("/user"); // 특정 사용자에게 메시지를 보낼 때 사용할 주소의 시작 부분
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompSubscriptionInterceptor);
    }
}
