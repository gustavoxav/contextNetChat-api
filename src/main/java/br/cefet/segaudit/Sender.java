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
    private static String gatewayIP;
    private static int gatewayPort;
    private MrUdpNodeConnection connection;
    private UUID myUUID;
    private UUID destinationUUID;
    private MessageApp messageApp;

    public Sender(String server, int port, UUID myUUID, UUID destinationUUID) {
        this.myUUID = myUUID;
        setDestinationUUID(destinationUUID);
        setGatewayIP(server);
        setGatewayPort(port);
        System.out.println("Conectando ao gateway " + server + ":" + port + " com UUID: " + myUUID + " e destino: "
                + destinationUUID);
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

    public void sendMessage(String msg) {

        InetSocketAddress address = new InetSocketAddress(getGatewayIP(), getGatewayPort());
        try {
            connection = new MrUdpNodeConnection(getMyUUID());
            connection.addNodeConnectionListener(this);
            connection.connect(address);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ApplicationMessage message = new ApplicationMessage();
        System.out.println("SENDING...: " + getDestinationUUID());
        System.out.println("Mensagem enviada: " + msg);
        System.out.println("Meu UUID: " + getMyUUID());
        message.setContentObject(msg);
        message.setRecipientID(getDestinationUUID());

        try {
            connection.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void newMessageReceived(NodeConnection remoteCon, Message message) {
        try {
            System.out.println("Confirmação: " + message.getContentObject());

            String received = (String) message.getContentObject();
            System.out.println("Mensagem recebida: " + received);
            EventQueue.invokeLater(() -> {
                messageApp.displayReceivedMessage(received);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UUID getDestinationUUID() {
        return destinationUUID;
    }

    public void setDestinationUUID(UUID strDestinationUUID) {
        this.destinationUUID = strDestinationUUID;
    }

    public static String getGatewayIP() {
        return gatewayIP;
    }

    public static void setGatewayIP(String gatewayIP) {
        Sender.gatewayIP = gatewayIP;
    }

    public static int getGatewayPort() {
        return gatewayPort;
    }

    public static void setGatewayPort(int gatewayPort) {
        Sender.gatewayPort = gatewayPort;
    }

    public UUID getMyUUID() {
        return myUUID;
    }

    public void setMyUUID(String strMyUUID) {
        this.myUUID = UUID.fromString(strMyUUID);
        ;
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
