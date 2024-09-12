package br.cefet.segaudit;

import javax.crypto.Cipher;
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

    private static String gatewayIP = "bsi.cefet-rj.br";
    private static int gatewayPort = 5500;
    private MrUdpNodeConnection connection;
    private UUID myUUID;

    // A chave pública do receiver, obtida a partir do MobileReceiver (em Base64)
    private static final String RECEIVER_PUBLIC_KEY_BASE64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtCZlxCZNGnSsMq58HGcbPAMewb/DGdtxuwkrRWhSOyE1JkqKfyYn0iEMIqE3652A/XvqnWqqiIIqZwvmvpz8PdoCdfYThKRmC8X6Ii39DnGTNqouANB3gWdKm67MCWA8H7Ra8t+gGL6NfbjZzKUcj7ITNgJPZ5ZGFBzfCBfKtaMuNcRPi2h0KVFeoHjBBnx7Zs1ph1MZN2HbIr4Ea8PAT+2y2saI6eqSJOKRfSGavKzGXJsccJZE+Qy8Ydw2Od/6P91L/YWYawxDVj+Q7jx29QdnvF5feDtlROFaHuJaGpAnJY79EaWtMg2hrYU1GBX00fxsK7Cu0ElFzofpXe95IQIDAQAB";

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
        sender.sendTextMessage("Hello, this is a secure message with asymmetric encryption!");
    }

    // Método para criptografar a mensagem usando a chave pública do receiver
    public String encrypt(String message, String publicKeyBase64) throws Exception {
        // Converter a chave pública de Base64 para PublicKey
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        // Criptografar a mensagem com a chave pública do receiver
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());

        return Base64.getEncoder().encodeToString(encryptedBytes);  // Retorna a mensagem criptografada em Base64
    }

    // Enviar a mensagem de texto criptografada
    public void sendTextMessage(String messageContent) {
        try {
            String encryptedMessage = encrypt(messageContent, RECEIVER_PUBLIC_KEY_BASE64);  // Criptografar a mensagem
            System.out.println("Encrypted message sender: " + encryptedMessage);

            ApplicationMessage message = new ApplicationMessage();
            message.setContentObject(encryptedMessage);
            message.setRecipientID(UUID.fromString("788b2b22-baa6-4c61-b1bb-01cff1f5f878"));

            connection.sendMessage(message);  // Enviar a mensagem criptografada
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
