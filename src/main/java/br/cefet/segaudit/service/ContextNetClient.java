package br.cefet.segaudit.service;

import br.cefet.segaudit.Sender;
import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.sddl.message.Message;

import java.util.function.Consumer;

import br.cefet.segaudit.dto.ContextNetConfig;

public class ContextNetClient implements NodeConnectionListener {
    private Sender sender;
    private Consumer<String> messageHandler;

    public ContextNetClient(ContextNetConfig config, Consumer<String> messageHandler) {
        this.messageHandler = messageHandler;
        System.out.println("Conectando ao gateway " + config.gatewayIP + ":" + config.gatewayPort);
        System.out.println("De: " + config.myUUID + " - Para: " + config.destinationUUID);
        sender = new Sender(
                config.gatewayIP,
                config.gatewayPort,
                config.myUUID,
                config.destinationUUID,
                this::handleIncomingMessage);
        sender.sendMessage(
                "<mid1,641f18ae-6c0c-45c2-972f-d37c309a9b72,tell,cc2528b7-fecc-43dd-a1c6-188546f0ccbf,numeroDaSorte(3337)>");
    }

    private void handleIncomingMessage(String message) {
        System.out.println("[WebSocket] Recebido da ContextNet: " + message);
        if (messageHandler != null) {
            System.out.println("Chamando messageHandler com: " + message);
            messageHandler.accept(message);
        } else {
            System.out.println("messageHandler é null!");
        }
    }

    public void setMessageHandler(Consumer<String> handler) {
        this.messageHandler = handler;
    }

    public void sendToContextNet(String message) {
        if (sender != null) {
            if (message.startsWith("\"") && message.endsWith("\"")) {
                message = message.substring(1, message.length() - 1);
            }

            System.out.println("[WebSocket] Enviando para ContextNet: " + message);
            sender.sendMessage(message);
        }
    }

    @Override
    public void newMessageReceived(NodeConnection arg0, Message message) {
        String received = (String) message.getContentObject();
        System.out.println("Nova mensagem recebida Intern: " + received);
    }

    @Override
    public void connected(NodeConnection remoteCon) {
    }

    @Override
    public void reconnected(NodeConnection remoteCon, java.net.SocketAddress endPoint, boolean wasHandover,
            boolean wasMandatory) {
    }

    @Override
    public void disconnected(NodeConnection remoteCon) {
    }

    @Override
    public void unsentMessages(NodeConnection remoteCon, java.util.List<Message> unsentMessages) {
    }

    @Override
    public void internalException(NodeConnection remoteCon, Exception e) {
    }
}
