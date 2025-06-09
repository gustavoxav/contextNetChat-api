package br.cefet.segaudit.controller;

import br.cefet.segaudit.service.ContextNetClient;
import br.cefet.segaudit.service.ContextNetService;

import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class ContextNetWebSocketHandler extends TextWebSocketHandler {
    private final ContextNetClient contextNetClient;
    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    public ContextNetWebSocketHandler(ContextNetClient contextNetClient) {
        this.contextNetClient = contextNetClient;
        this.contextNetClient.setMessageHandler(this::broadcastMessageToAll);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
       System.out.println("Mensagem recebida do WebSocket: " + message.getPayload());
        contextNetClient.sendToContextNet(message.getPayload());
    }

    private void broadcastMessageToAll(String msg) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(msg));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
