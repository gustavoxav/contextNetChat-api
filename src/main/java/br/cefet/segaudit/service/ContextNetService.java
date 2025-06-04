package br.cefet.segaudit.service;

import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.Message;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

@Service
public class ContextNetService implements NodeConnectionListener {

    private MrUdpNodeConnection connection;
    private WebSocketSession currentSession;
    private final UUID myUUID = UUID.randomUUID();
    private final UUID destinationUUID = UUID.fromString("00000000-0000-0000-0000-000000000000"); // substitua

    public ContextNetService() {
        try {
            InetSocketAddress address = new InetSocketAddress("127.0.0.1", 5500); // substitua conforme necessário
            connection = new MrUdpNodeConnection(myUUID);
            connection.addNodeConnectionListener(this);
            connection.connect(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerSession(WebSocketSession session) {
        this.currentSession = session;
        sendToContextNet("Conexão registrada via WebSocket");
    }

    public void unregisterSession(WebSocketSession session) {
        if (session == this.currentSession) {
            this.currentSession = null;
        }
    }

    public void processIncomingMessage(WebSocketSession session, String payload) {
        sendToContextNet(payload);
    }

    private void sendToContextNet(String payload) {
        ApplicationMessage appMessage = new ApplicationMessage();
        appMessage.setContentObject(payload);
        appMessage.setRecipientID(destinationUUID);
        try {
            connection.sendMessage(appMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Recebendo mensagens do ContextNet
    @Override
    public void newMessageReceived(NodeConnection remoteCon, Message message) {
        try {
            String content = (String) message.getContentObject();
            if (currentSession != null && currentSession.isOpen()) {
                currentSession.sendMessage(new TextMessage("Recebido do ContextNet: " + content));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Outros métodos obrigatórios da interface
    @Override
    public void connected(NodeConnection remoteCon) {
    }

    @Override
    public void disconnected(NodeConnection remoteCon) {
    }

    @Override
    public void reconnected(NodeConnection remoteCon, java.net.SocketAddress endPoint, boolean wasHandover,
            boolean wasMandatory) {
    }

    @Override
    public void unsentMessages(NodeConnection remoteCon, List<Message> unsentMessages) {
    }

    @Override
    public void internalException(NodeConnection remoteCon, Exception e) {
    }
}
