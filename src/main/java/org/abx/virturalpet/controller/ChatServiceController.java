package org.abx.virturalpet.controller;

import java.util.List;
import org.abx.virturalpet.dto.SendMessageDto;
import org.abx.virturalpet.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messages")
public class ChatServiceController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/send")
    public ResponseEntity<SendMessageDto> sendMessage(@RequestBody SendMessageDto sendMessageDto) {
        SendMessageDto response = chatService.sendMessage(sendMessageDto);
        if (response.getStatusCode() == 0) {
            return ResponseEntity.status(201).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/receive/{userId}")
    public ResponseEntity<List<SendMessageDto>> fetchMessages(@PathVariable int userId) {
        List<SendMessageDto> messages = chatService.fetchMessages(userId);
        if (messages.isEmpty() || messages.get(0).getStatusCode() != 0) {
            return ResponseEntity.status(404).body(messages);
        }
        return ResponseEntity.ok(messages);
    }
}
