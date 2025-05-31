package br.cefet.segaudit.queue;

import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageQueue {
    private static final ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<>();

    public static void addMessage(String message) {
        messages.add(message);
    }

    public static String pollMessage() {
        return messages.poll();
    }

    public static boolean hasMessages() {
        return !messages.isEmpty();
    }
}
