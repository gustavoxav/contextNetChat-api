package br.cefet.segaudit;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.Message;

public class MobileReceiver implements NodeConnectionListener {

    private static String       gatewayIP    = "bsi.cefet-rj.br";
    private static int          gatewayPort  = 5500;
    private MrUdpNodeConnection connection;
    private UUID                myUUID;
    private static final String SECRET_KEY = "segaudit12345678";

    public MobileReceiver() {
        myUUID = UUID.fromString("788b2b22-baa6-4c61-b1bb-01cff1f5f878");  
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

        MobileReceiver receiver = new MobileReceiver();
    }

    public String decrypt(String encryptedMessage) throws Exception {
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = Base64.getDecoder().decode(encryptedMessage);
        return new String(cipher.doFinal(decryptedBytes));
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
        try {
            String encryptedMessage = (String) message.getContentObject();
            System.out.println("Received encrypted message: " + encryptedMessage);

            String decryptedMessage = decrypt(encryptedMessage);
            System.out.println("Decrypted message: " + decryptedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reconnected(NodeConnection remoteCon, java.net.SocketAddress endPoint, boolean wasHandover, boolean wasMandatory) {}

    public void disconnected(NodeConnection remoteCon) {}

    public void unsentMessages(NodeConnection remoteCon, List<Message> unsentMessages) {}

    public void internalException(NodeConnection remoteCon, Exception e) {}
}
