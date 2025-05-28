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

public class Receiver implements NodeConnectionListener {

    private static String gatewayIP;
    private static int gatewayPort;
    private MrUdpNodeConnection connection;
    private UUID myUUID;
    private MessageApp messageApp;

    public Receiver(String server, int port, UUID myUUID) {
        this.myUUID = myUUID;
        setGatewayIP(server);
        setGatewayPort(port);

        EventQueue.invokeLater(() -> {
            messageApp = new MessageApp();
        });
    }

    public void listen() {
        InetSocketAddress address = new InetSocketAddress(getGatewayIP(), getGatewayPort());
        try {
            connection = new MrUdpNodeConnection(getMyUUID());
            connection.addNodeConnectionListener(this);
            connection.connect(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
    public void newMessageReceived(NodeConnection remoteCon, Message message) {
        String received = (String) message.getContentObject();
        System.out.println("Mensagem Recebida: " + message.getContentObject());

        ApplicationMessage appMessage = new ApplicationMessage();
        appMessage.setContentObject("Mensagem de confirmação de recepção no destino!");
        appMessage.setRecipientID(message.getSenderID());

        try {
            connection.sendMessage(appMessage);
            EventQueue.invokeLater(() -> {
                messageApp.displayReceivedMessage(received);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // public void newMessageReceived(NodeConnection remoteCon, Message message) {
    // String received = (String) message.getContentObject();
    // System.out.println("Mensagem Recebida: " + received);

    // ApplicationMessage appMessage = new ApplicationMessage();
    // appMessage.setContentObject("Mensagem de confirmação de recepção no
    // destino!");
    // appMessage.setRecipientID(message.getSenderID());

    // try {
    // connection.sendMessage(appMessage);
    // EventQueue.invokeLater(() -> {
    // messageApp.displayReceivedMessage(received);
    // });
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }

    public static String getGatewayIP() {
        return gatewayIP;
    }

    public static void setGatewayIP(String gatewayIP) {
        Receiver.gatewayIP = gatewayIP;
    }

    public static int getGatewayPort() {
        return gatewayPort;
    }

    public static void setGatewayPort(int gatewayPort) {
        Receiver.gatewayPort = gatewayPort;
    }

    public UUID getMyUUID() {
        return myUUID;
    }

    public void setMyUUID(String strMyUUID) {
        this.myUUID = UUID.fromString(strMyUUID);
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
        System.out.println("Unsent messages: " + unsentMessages.size());
        for (Message msg : unsentMessages) {
            System.out.println("Unsent message: " + msg.getContentObject());
        }
    }

    @Override
    public void internalException(NodeConnection remoteCon, Exception e) {
    }

}
