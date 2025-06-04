package br.cefet.segaudit.config;

import br.cefet.segaudit.controller.ContextNetWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final ContextNetWebSocketHandler handler;

    public WebSocketConfig(ContextNetWebSocketHandler handler) {
        this.handler = handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws").setAllowedOrigins("*");
    }
}
