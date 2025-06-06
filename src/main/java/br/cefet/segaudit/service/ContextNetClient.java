package br.cefet.segaudit.service;

import br.cefet.segaudit.Sender;
import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.sddl.message.Message;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.function.Consumer;

@Service
public class ContextNetClient implements NodeConnectionListener {
    private Sender sender;
    private Consumer<String> messageHandler;

    private final UUID myUUID = UUID.fromString("cc2528b7-fecc-43dd-a1c6-188546f0ccbf");
    private final UUID destinationUUID = UUID.fromString("641f18ae-6c0c-45c2-972f-d37c309a9b72");
    private final String gatewayIP = "bsi.cefet-rj.br";
    private final int gatewayPort = 5500;

    public ContextNetClient() {
        System.out.println("Conectando ao gateway " + gatewayIP + ":" + gatewayPort);
        System.out.println("De: " + myUUID + " - Para: " + destinationUUID);
        sender = new Sender(gatewayIP, gatewayPort, myUUID, destinationUUID,  this::handleIncomingMessage);
    }

    private void handleIncomingMessage(String message) {
        System.out.println("[WebSocket] Recebido da ContextNet: " + message);
        if (messageHandler != null) {
            messageHandler.accept(message);
        }
    }

    public void setMessageHandler(Consumer<String> handler) {
        this.messageHandler = handler;
    }

    public void sendToContextNet(String message) {
        if (sender != null) {
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
