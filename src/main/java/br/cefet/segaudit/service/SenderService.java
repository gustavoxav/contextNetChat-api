package br.cefet.segaudit.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

import br.cefet.segaudit.dto.MessageDTO;
import br.cefet.segaudit.queue.MessageQueue;
import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.Message;

public class SenderService implements NodeConnectionListener {
    private MrUdpNodeConnection connection;
    private UUID userUUID;
    private boolean connected = false;

    public SenderService(UUID userUUID, String serverAddress, int serverPort) throws IOException {
        InetSocketAddress address = new InetSocketAddress(serverAddress, serverPort);

        this.userUUID = userUUID;
        connection = new MrUdpNodeConnection(userUUID);
        connection.addNodeConnectionListener(this);
        connection.connect(address);
        connected = true;
    }

    public void sendMessage(MessageDTO messageDTO) throws IOException {
        if (!connected) {
            throw new IllegalStateException("Conexão não estabelecida.");
        }
        ApplicationMessage message = new ApplicationMessage();
        message.setContentObject(messageDTO.getContent());
        message.setRecipientID(messageDTO.getReceiverUuid());
        connection.sendMessage(message);
    }

    @Override
    public void connected(NodeConnection nodeConnection) {
        System.out.println("Conectado ao servidor ContextNet.");
    }

    @Override
    public void newMessageReceived(NodeConnection nodeConnection, Message message) {
        String receivedContent = new String(message.getContent());
        System.out.println("Mensagem recebida de : " + receivedContent);
        //MessageQueue.getInstance().addMessage(sender, receivedContent);
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
