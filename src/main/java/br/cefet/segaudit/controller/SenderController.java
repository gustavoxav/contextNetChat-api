package br.cefet.segaudit.controller;

import br.cefet.segaudit.dto.MessageDTO;
import br.cefet.segaudit.queue.MessageQueue;
import br.cefet.segaudit.service.SenderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
public class SenderController {

    @PostMapping("/send")
    public String sendMessage(@RequestBody MessageDTO dto) {
        try {
            SenderService senderService = new SenderService(dto.getSenderUuid(), "bsi.cefet-rj.br", 5500);
            senderService.sendMessage(dto);
            return "Mensagem enviada." + dto.getContent() + " para " + dto.getReceiverUuid();
        } catch (Exception e) {
            // TODO: handle exception
            return "Erro ao enviar mensagem: " + e.getMessage();
        }
    }

    @GetMapping("/receive")
    public String receiveMessage() {
        if (MessageQueue.hasMessages()) {
            return MessageQueue.pollMessage();
        } else {
            return "Sem mensagens novas.";
        }
    }
}
