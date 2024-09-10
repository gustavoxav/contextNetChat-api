package br.cefet.segaudit;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Base64;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.sddl.message.ApplicationMessage;

public class MobileSender implements NodeConnectionListener {

    private static String       gatewayIP    = "bsi.cefet-rj.br";
    private static int          gatewayPort  = 5500;
    private MrUdpNodeConnection connection;
    private UUID                myUUID;
    private static final String SECRET_KEY = "segaudit12345678"; // Chave de 16 bytes (128 bits)

    public MobileSender() {
        myUUID = UUID.fromString("bb103877-8335-444a-be5f-db8d916f6754");
        InetSocketAddress address = new InetSocketAddress(gatewayIP, gatewayPort);
        try {
            connection = new MrUdpNodeConnection(myUUID);
            connection.addNodeConnectionListener(this);
            connection.connect(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Logger.getLogger("").setLevel(Level.ALL);

        MobileSender sender = new MobileSender();
        sender.sendTextMessage("Hello, mensagem segura para comunicação!");
    }

    public String encrypt(String message) throws Exception {
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public void sendTextMessage(String messageContent) {
        try {
            String encryptedMessage = encrypt(messageContent);
            System.out.println("Encrypted message: " + encryptedMessage);

            ApplicationMessage message = new ApplicationMessage();
            message.setContentObject(encryptedMessage);
            message.setRecipientID(UUID.fromString("788b2b22-baa6-4c61-b1bb-01cff1f5f878"));  // UUID do destinatário

            connection.sendMessage(message);  // Enviar mensagem criptografada
        } catch (Exception e) {
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

    public void newMessageReceived(NodeConnection remoteCon, lac.cnclib.sddl.message.Message message) {}

    public void reconnected(NodeConnection remoteCon, java.net.SocketAddress endPoint, boolean wasHandover, boolean wasMandatory) {}

    public void disconnected(NodeConnection remoteCon) {}

    public void unsentMessages(NodeConnection remoteCon, java.util.List<lac.cnclib.sddl.message.Message> unsentMessages) {}

    public void internalException(NodeConnection remoteCon, Exception e) {}
}
