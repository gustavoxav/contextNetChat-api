package br.cefet.segaudit;

import javax.crypto.Cipher;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.Message;

public class MobileReceiver implements NodeConnectionListener {

    private static String gatewayIP = "bsi.cefet-rj.br";
    private static int gatewayPort = 5500;
    private MrUdpNodeConnection connection;
    private UUID myUUID;

    // Chaves do receiver
    private PrivateKey privateKey;
    private PublicKey publicKey;

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

        // Gera o par de chaves e utiliza a chave privada e pública
        KeyPair keyPair = generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();

        // Exibe a chave pública para que o Sender possa utilizá-la
        System.out.println("Receiver Public Key (Base64): " + Base64.getEncoder().encodeToString(publicKey.getEncoded()));
    }

    public static void main(String[] args) {
        Logger.getLogger("").setLevel(Level.ALL);
        new MobileReceiver();
    }

    // Gera um par de chaves (pública e privada) para o receiver
    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            return keyGen.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Método para descriptografar a mensagem usando a chave privada do receiver
    public String decrypt(String encryptedMessage, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));

        return new String(decryptedBytes);  // Retorna a mensagem descriptografada
    }

    @Override
    public void newMessageReceived(NodeConnection remoteCon, Message message) {
        try {
            String encryptedMessage = (String) message.getContentObject();
            String decryptedMessage = decrypt(encryptedMessage, privateKey);  // Descriptografar a mensagem
            System.out.println("Decrypted message: " + decryptedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connected(NodeConnection remoteCon) {}

    public void reconnected(NodeConnection remoteCon, java.net.SocketAddress endPoint, boolean wasHandover, boolean wasMandatory) {}

    public void disconnected(NodeConnection remoteCon) {}

    public void unsentMessages(NodeConnection remoteCon, java.util.List<lac.cnclib.sddl.message.Message> unsentMessages) {}

    public void internalException(NodeConnection remoteCon, Exception e) {}
}
