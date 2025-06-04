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
    private final UUID myUUID = UUID.fromString("cc2528b7-fecc-43dd-a1c6-188546f0ccbf");
    private final UUID destinationUUID = UUID.fromString("641f18ae-6c0c-45c2-972f-d37c309a9b72");
    private static String gatewayIP = "bsi.cefet-rj.br";
    private static int gatewayPort = 5500;

    public ContextNetService() {
        try {
            InetSocketAddress address = new InetSocketAddress(gatewayIP, gatewayPort);
            connection = new MrUdpNodeConnection(myUUID);
            connection.addNodeConnectionListener(this);
            System.out.println("Conectando ao gateway " + myUUID + " e destino: " + destinationUUID);
            connection.connect(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerSession(WebSocketSession session) {
        // this.currentSession = session;
        // sendToContextNet("Conexão registrada via WebSocket");
    }

    public void unregisterSession(WebSocketSession session) {
        System.out.println("Desconectando sessão WebSocket: " + session.getId());
        // if (session == this.currentSession) {
        // this.currentSession = null;
        // }
    }

    public void processIncomingMessage(WebSocketSession session, String payload) {
        System.out.println("Processando mensagem recebida: " + payload);
        sendToContextNet(payload);
    }

    private void sendToContextNet(String payload) {

        InetSocketAddress address = new InetSocketAddress(gatewayIP, gatewayPort);
        try {
            connection = new MrUdpNodeConnection(myUUID);
            connection.addNodeConnectionListener(this);
            connection.connect(address);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ApplicationMessage appMessage = new ApplicationMessage();
        appMessage.setContentObject(payload);
        appMessage.setRecipientID(destinationUUID);
        System.out.println("Enviando mensagem para ContextNet: " + payload + " para " + destinationUUID);
        try {
            connection.sendMessage(appMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void newMessageReceived(NodeConnection remoteCon, Message message) {
        try {
            System.out.println("Confirmação: " + message.getContentObject());

            String content = (String) message.getContentObject();
            System.out.println("Mensagem recebida do ContextNet: " + content);
            if (currentSession != null && currentSession.isOpen()) {
                currentSession.sendMessage(new TextMessage("Recebido do ContextNet: " + content));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connected(NodeConnection remoteCon) {
        ApplicationMessage message = new ApplicationMessage();
        message.setContentObject("Registering");

        try {
            connection.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
