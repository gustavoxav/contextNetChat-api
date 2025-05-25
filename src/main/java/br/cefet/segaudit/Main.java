package br.cefet.segaudit;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Comando inválido.");
            System.out.println("Receiver: java -jar app.jar receiver [server] [port] [my-uuid]");
            System.out.println("Sender: java -jar app.jar sender [server] [port] [my-uuid] [receiver-uuid]");
            return;
        }

        Logger.getLogger("").setLevel(Level.ALL);

        String mode = args[0];
        String server = args[1];
        int port = Integer.parseInt(args[2]);
        UUID myUUID = UUID.fromString(args[3]);

        if (mode.equalsIgnoreCase("receiver")) {
            new Receiver(server, port, myUUID);
        } else if (mode.equalsIgnoreCase("sender")) {
            if (args.length < 5) {
                System.out.println("Uso incorreto do modo sender:");
                System.out.println("java -jar app.jar sender [server] [port] [my-uuid] [receiver-uuid]");
                return;
            }
            UUID receiverUUID = UUID.fromString(args[4]);
            new Sender(server, port, myUUID, receiverUUID);
        } else {
            System.out.println("Modo não reconhecido: " + mode);
        }
    }
}
