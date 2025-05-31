package br.cefet.segaudit.dto;

import java.util.UUID;

public class MessageDTO {
    private UUID senderUuid;
    private UUID receiverUuid;
    private String content;

    public UUID getSenderUuid() {
        return senderUuid;
    }

    public void setSenderUuid(UUID senderUuid) {
        this.senderUuid = senderUuid;
    }

    public UUID getReceiverUuid() {
        return receiverUuid;
    }

    public void setReceiverUuid(UUID receiverUuid) {
        this.receiverUuid = receiverUuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
