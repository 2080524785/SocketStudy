package com.brave.www.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        // 注册websocket实现类，指定参数访问地址;allowed-origins="*" 允许跨域
        // addHandler是增加处理接口和设定URL
        // addInterceptors是增加拦截器处理（可以不用）
//        webSocketHandlerRegistry.addHandler(videoClientHandler(), "/ws").addInterceptors(videoClientHandshakeInterceptor()).setAllowedOrigins("*");
//        webSocketHandlerRegistry.addHandler(videoClientHandler(), "/sockjs/ws").addInterceptors(videoClientHandshakeInterceptor()).withSockJS();

        webSocketHandlerRegistry.addHandler(videoClientHandler(), "/websocket").setAllowedOrigins("**");
        webSocketHandlerRegistry.addHandler(videoClientHandler(), "/sockjs/websocket").setAllowedOrigins("**").withSockJS();
    }

    @Bean
    public VideoClientHandler videoClientHandler() {
        return new VideoClientHandler();
    }

}
