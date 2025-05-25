package br.cefet.segaudit;

import java.awt.EventQueue;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.Message;

public class Sender implements NodeConnectionListener {
    private MrUdpNodeConnection connection;
    private UUID myUUID;
    private UUID receiverUUID;
    private String messageToSend;
    private MessageApp messageApp;

    public Sender(String server, int port, UUID myUUID, UUID receiverUUID) {
        this.myUUID = myUUID;
        this.receiverUUID = receiverUUID;

        // connect(server, port);
        EventQueue.invokeLater(() -> {
            messageApp = new MessageApp(this);
            InetSocketAddress address = new InetSocketAddress(server, port);
            try {
                connection = new MrUdpNodeConnection(this.myUUID);
                connection.addNodeConnectionListener(this);
                connection.connect(address);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void connect(String server, int port) {
        EventQueue.invokeLater(() -> {
            InetSocketAddress address = new InetSocketAddress(server, port);
            try {
                connection = new MrUdpNodeConnection(this.myUUID);
                connection.addNodeConnectionListener(this);
                connection.connect(address);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void connected(NodeConnection remoteCon) {
        System.out.println("Conectado ao servidor!");
    }

    public void sendMessage(String messageContent) {
        try {
            ApplicationMessage message = new ApplicationMessage();
            message.setContentObject(messageContent);
            message.setRecipientID(receiverUUID);
            connection.sendMessage(message);
            System.out.println("Receiver: " + receiverUUID);
            System.out.println("Mensagem enviada: " + messageContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void newMessageReceived(NodeConnection remoteCon, Message message) {
        try {
            String received = (String) message.getContentObject();
            System.out.println("Mensagem recebida: " + received);
            EventQueue.invokeLater(() -> {
                messageApp.displayReceivedMessage(received);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
