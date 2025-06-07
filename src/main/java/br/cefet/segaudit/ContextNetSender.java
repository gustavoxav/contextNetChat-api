package br.cefet.segaudit;

import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.UUID;

public class ContextNetSender implements NodeConnectionListener {

    private String gatewayIP;
    private int gatewayPort;
    private MrUdpNodeConnection connection;
    private UUID myUUID;
    private UUID destinationUUID;

    ContextNetSender(String strMyUUID, String strDestinationUUID, String strGatewayIP, int intGatewayPort) {
        this.myUUID = UUID.fromString(strMyUUID);
        this.destinationUUID = UUID.fromString(strDestinationUUID);
        this.gatewayIP = strGatewayIP;
        this.gatewayPort = intGatewayPort;
    }

    public void setDestinationUUID(String strDestinationUUID) {
        this.destinationUUID = UUID.fromString(strDestinationUUID);
    }

    public void sendMessage(String msg) {

        InetSocketAddress address = new InetSocketAddress(this.gatewayIP, this.gatewayPort);
        try {
            connection = new MrUdpNodeConnection(this.myUUID);
            connection.addNodeConnectionListener(this);
            connection.connect(address);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ApplicationMessage message = new ApplicationMessage();
        message.setContentObject(msg);
        message.setRecipientID(this.destinationUUID);

        try {
            connection.sendMessage(message);
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

    public void newMessageReceived(NodeConnection remoteCon, Message message) {
        System.out.println("Confirmação: " + message.getContentObject());
    }

    public void reconnected(NodeConnection remoteCon, SocketAddress endPoint, boolean wasHandover,
            boolean wasMandatory) {
    }

    public void disconnected(NodeConnection remoteCon) {
    }

    public void unsentMessages(NodeConnection remoteCon, List<Message> unsentMessages) {
    }

    public void internalException(NodeConnection remoteCon, Exception e) {
    }
}
