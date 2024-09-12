package br.cefet.segaudit;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
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

    private static final String RECEIVER_PUBLIC_KEY_BASE64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp2689QS6X7xH6aUqZyZdXoBO+r9RlevFSkWnT0l5iPlWn8snCYYLMfUeeeP8gH5p5baSpgaajUA5ucvlMKrHN4SXj+ejbtgr+p/ojzcAEv8yQoJpKsWNxuohjpSrZt9cpTmC2qp1kTRkxzXabNt6asJy8xyy6lfrJRo8z8jbMnWGq5CzpdS1Api3la2bhFhEYET3dV7JWfufbbsEumoH4iGbHSBYm7CNerEFGO2BPxdUTAp2JI+fSKA46np7gv308qPuOE0yvJGDA4cG8DNEslWM9+wqOtlqxAJGnHPUzwz6qdj36qkGYeRDfezq/NQBzUUVJLvuzR/mXmMD2pfgCwIDAQAB";
    
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
        sender.sendTextMessage("Mensagem segura para comunicação assíncrona!");
    }

    public String encrypt(String message, String publicKeyBase64) throws Exception {
        // Converter a chave pública de Base64 para PublicKey
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public void sendTextMessage(String messageContent) {
        try {
            String encryptedMessage = encrypt(messageContent, RECEIVER_PUBLIC_KEY_BASE64);
            System.out.println("Encrypted message: " + encryptedMessage);

            ApplicationMessage message = new ApplicationMessage();
            message.setContentObject(encryptedMessage);
            message.setRecipientID(UUID.fromString("788b2b22-baa6-4c61-b1bb-01cff1f5f878"));

            connection.sendMessage(message);
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
