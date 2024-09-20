package br.cefet.segaudit;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import java.awt.EventQueue;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.Message;

public class Comunicador implements NodeConnectionListener {

    private MrUdpNodeConnection connection;
    private UUID myUUID;
    private PrivateKey myPrivateKey;
    private UUID receiverUUID;
    private PublicKey receiverPublicKey;
    private MessageApp messageApp;
    private String simetricKey;
    private boolean isSimetricMode;
    
    public Comunicador(String server, int port, UUID myUUID, PrivateKey myPrivateKey, UUID receiverUUID, PublicKey receiverPublicKey) {
        this.myUUID = myUUID;
        this.myPrivateKey = myPrivateKey;
        this.receiverUUID = receiverUUID;
        this.receiverPublicKey = receiverPublicKey;
        this.isSimetricMode = false; 
        
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

    public Comunicador(String server, int port, UUID myUUID, UUID receiverUUID, String key) {
        this.myUUID = myUUID;
        this.receiverUUID = receiverUUID;
        this.simetricKey = key;
        this.isSimetricMode = true;
        
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
    

    public static void main(String[] args) {
        if (args.length < 6) {
            System.out.println("Usage: app.jar run [server] [port] [my-uuid] [my-privateKey] [receiver-uuid] [receiver-publicKey]");
            return;
        }

        Logger.getLogger("").setLevel(Level.ALL);
        
        String server = args[1];
        int port = Integer.parseInt(args[2]);
        
        if ("run".equals(args[0])) {
        UUID myUUID = UUID.fromString(args[3]);
        PrivateKey myPrivateKey = loadPrivateKey(args[4]);
        UUID receiverUUID = UUID.fromString(args[5]);
        PublicKey receiverPublicKey = loadPublicKey(args[6]);
        
        new Comunicador(server, port, myUUID, myPrivateKey, receiverUUID, receiverPublicKey);
        } else if ("runSimetric".equals(args[0])) {
            UUID myUUID = UUID.fromString(args[3]);
            UUID receiverUUID = UUID.fromString(args[4]);
            String simetricKey = args[5];
            new Comunicador(server, port, myUUID, receiverUUID, simetricKey);
        }
    }

    public String encryptSimetric(String message) throws Exception {
        SecretKeySpec key = new SecretKeySpec(this.simetricKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decryptSimetric(String encryptedMessage) throws Exception {
    	SecretKeySpec key = new SecretKeySpec(this.simetricKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = Base64.getDecoder().decode(encryptedMessage);
        return new String(cipher.doFinal(decryptedBytes));
    }
    
    private static PrivateKey loadPrivateKey(String privateKeyFilePath) {
        try {
            String key = new String(Files.readAllBytes(Paths.get(privateKeyFilePath)));
            key = key.replace("-----BEGIN PRIVATE KEY-----", "")
                     .replace("-----END PRIVATE KEY-----", "")
                     .replaceAll("\\s", "");

            byte[] keyBytes = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar chave privada", e);
        }
    }

    private static PublicKey loadPublicKey(String publicKeyFilePath) {
        try {
            String key = new String(Files.readAllBytes(Paths.get(publicKeyFilePath)));
            key = key.replace("-----BEGIN PUBLIC KEY-----", "")
                     .replace("-----END PUBLIC KEY-----", "")
                     .replaceAll("\\s", "");

            byte[] keyBytes = Base64.getDecoder().decode(key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar chave pública", e);
        }
    }

    public String encryptAsymmetric(String message) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, receiverPublicKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decryptAsymmetric(String encryptedMessage) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, myPrivateKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
        return new String(decryptedBytes);
    }

    public void sendMessage(String messageContent) {
        try {
        	String encryptedMessage;
            if (isSimetricMode) {
                encryptedMessage = encryptSimetric(messageContent);  // Usa a criptografia simétrica
            } else {
                encryptedMessage = encryptAsymmetric(messageContent);  // Usa a criptografia assimétrica
            }
            ApplicationMessage message = new ApplicationMessage();
            message.setContentObject(encryptedMessage);
            message.setRecipientID(receiverUUID);
            connection.sendMessage(message);
            System.out.println("Mensagem enviada: " + messageContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void newMessageReceived(NodeConnection remoteCon, Message message) {
        try {
            String encryptedMessage = (String) message.getContentObject();
            System.out.println("Mensagem criptografada recebida: " + encryptedMessage);

            String decryptedMessage;
            if (isSimetricMode) {
                decryptedMessage = decryptSimetric(encryptedMessage);
            } else {
                decryptedMessage = decryptAsymmetric(encryptedMessage);
            }
            
            System.out.println("Mensagem descriptografada: " + decryptedMessage);

            EventQueue.invokeLater(() -> {
            	messageApp.displayReceivedMessage(decryptedMessage);
            });
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connected(NodeConnection remoteCon) {
        System.out.println("Conectado ao servidor!");
    }

    @Override
    public void reconnected(NodeConnection remoteCon, java.net.SocketAddress endPoint, boolean wasHandover, boolean wasMandatory) {}

    @Override
    public void disconnected(NodeConnection remoteCon) {}

    @Override
    public void unsentMessages(NodeConnection remoteCon, java.util.List<Message> unsentMessages) {}

    @Override
    public void internalException(NodeConnection remoteCon, Exception e) {}
}
