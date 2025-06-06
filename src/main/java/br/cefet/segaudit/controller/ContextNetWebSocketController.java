package br.cefet.segaudit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import br.cefet.segaudit.service.ContextNetClient;

import java.util.UUID;

@Controller
public class ContextNetWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ContextNetClient contextNetClient;

    @Autowired
    public ContextNetWebSocketController(SimpMessagingTemplate messagingTemplate, ContextNetClient contextNetClient) {
        this.messagingTemplate = messagingTemplate;
        this.contextNetClient = contextNetClient;

        contextNetClient.setMessageHandler(message -> {
            System.out.println("Enviando ao WebSocket: " + message);
            messagingTemplate.convertAndSend("/topic/messages", message);
        });
    }

    @MessageMapping("/send")
    public void receiveMessage(String message) {
        System.out.println("Mensagem recebida do WebSocket: " + message);
        contextNetClient.sendToContextNet(message);
    }
}

