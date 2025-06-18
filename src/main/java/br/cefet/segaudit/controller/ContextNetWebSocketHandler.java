package br.cefet.segaudit.controller;

import br.cefet.segaudit.dto.ContextNetConfig;
import br.cefet.segaudit.factory.ContextNetClientFactory;
import br.cefet.segaudit.service.ContextNetClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ContextNetWebSocketHandler extends TextWebSocketHandler {
    private final ContextNetClientFactory contextNetClientFactory;
    private final Map<String, ContextNetClient> clients = new ConcurrentHashMap<>();
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    public ContextNetWebSocketHandler(ContextNetClientFactory factory) {
        this.contextNetClientFactory = factory;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        clients.remove(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        try {

            if (!clients.containsKey(session.getId())) {
                ContextNetConfig config = new ObjectMapper().readValue(payload, ContextNetConfig.class);

                ContextNetClient client = contextNetClientFactory.create(config, (msg) -> {
                    sendToSession(session, msg);
                });

                clients.put(session.getId(), client);
            } else {
                ContextNetClient client = clients.get(session.getId());
                client.sendToContextNet(payload);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendToSession(WebSocketSession session, String msg) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(msg));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}