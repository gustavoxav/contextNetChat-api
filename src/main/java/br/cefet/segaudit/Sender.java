package br.cefet.segaudit;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.function.Consumer;

import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.Message;

public class Sender implements NodeConnectionListener {
    private String gatewayIP;
    private int gatewayPort;
    private MrUdpNodeConnection connection;
    private UUID myUUID;
    private UUID destinationUUID;
    private Consumer<String> onMessageReceived;
    private NodeConnectionListener externalListener;

    public Sender(String server, int port, UUID myUUID, UUID destinationUUID, Consumer<String> onMessageReceived) {
        this.myUUID = myUUID;
        this.destinationUUID = destinationUUID;
        this.gatewayIP = server;
        this.gatewayPort = port;
        this.onMessageReceived = onMessageReceived;
        System.out.println(
                "1- IN SENDER Conectando ao gateway " + server + ":" + port + " com UUID: " + myUUID + " e destino: "
                        + destinationUUID);

        InetSocketAddress address = new InetSocketAddress(server, port);
        try {
            connection = new MrUdpNodeConnection(this.myUUID);
            connection.addNodeConnectionListener(this);
            connection.connect(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connected(NodeConnection remoteCon) {
        ApplicationMessage message = new ApplicationMessage();
        try {
            connection.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (externalListener != null) {
            externalListener.connected(remoteCon);
        }
    }

    public void sendMessage(String msg) {
        ApplicationMessage message = new ApplicationMessage();
        System.out.println("Mensagem enviada: " + msg + "  para: " + getDestinationUUID() + " - " + getMyUUID());
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
            String received = (String) message.getContentObject();
            System.out.println("IN SENDER Mensagem recebida: " + received);
            if (onMessageReceived != null) {
                onMessageReceived.accept(received);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setConnectionListener(NodeConnectionListener listener) {
        this.externalListener = listener;
    }

    public UUID getDestinationUUID() {
        return destinationUUID;
    }

    public void setDestinationUUID(UUID strDestinationUUID) {
        this.destinationUUID = strDestinationUUID;
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
    }

    @Override
    public void internalException(NodeConnection remoteCon, Exception e) {
    }
}
