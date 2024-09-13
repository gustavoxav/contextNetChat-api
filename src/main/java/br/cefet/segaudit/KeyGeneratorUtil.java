package br.cefet.segaudit;

import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class KeyGeneratorUtil {

    public static void main(String[] args) {
        if (args.length < 2 || !args[0].equalsIgnoreCase("generateKeys")) {
            System.out.println("Uso correto: java -jar app.jar generateKeys [nome do arquivo]");
            return;
        }

        String filename = args[1];
        generateAndSaveKeys(filename);
    }

    private static void generateAndSaveKeys(String filename) {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            String publicKeyEncoded = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String privateKeyEncoded = Base64.getEncoder().encodeToString(privateKey.getEncoded());

            saveKeyToFile(filename + ".publicKey", publicKeyEncoded);
            saveKeyToFile(filename + ".privateKey", privateKeyEncoded);

            System.out.println("Chaves geradas e salvas com sucesso.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveKeyToFile(String filename, String key) {
        try (FileOutputStream fos = new FileOutputStream(new File(filename))) {
            fos.write(key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
