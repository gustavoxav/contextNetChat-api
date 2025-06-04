package br.cefet.segaudit.controller;

import br.cefet.segaudit.service.ContextNetService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class ContextNetWebSocketHandler extends TextWebSocketHandler {

    private final ContextNetService contextNetService;

    public ContextNetWebSocketHandler(ContextNetService contextNetService) {
        this.contextNetService = contextNetService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("Conexão WebSocket estabelecida: " + session.getId());
        contextNetService.registerSession(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        contextNetService.processIncomingMessage(session, message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        contextNetService.unregisterSession(session);
    }
}
