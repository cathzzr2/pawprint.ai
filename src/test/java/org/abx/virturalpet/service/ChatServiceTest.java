package org.abx.virturalpet.service;

import org.abx.virturalpet.dto.SendMessageDto;
import org.abx.virturalpet.model.MessageModel;
import org.abx.virturalpet.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

public class ChatServiceTest {

    private final MessageRepository messageRepository = Mockito.mock(MessageRepository.class);
    private final ChatService chatService = new ChatService(messageRepository);

    @Test
    public void testSendMessage() {
        SendMessageDto sendMessageDto = SendMessageDto.builder()
                .userId(UUID.randomUUID())
                .threadId(UUID.randomUUID())
                .messageContent("Hello, how are you?")
                .build();

        chatService.sendMessage(sendMessageDto);
        MessageModel messageModel = MessageModel.builder()
                .userId(sendMessageDto.getUserId())
                .threadId(sendMessageDto.getThreadId())
                .message(sendMessageDto.getMessageContent())
                .build();

    }

    @Test
    public void testFetchMessagesByUserId() {
        UUID userId = UUID.randomUUID();
        chatService.fetchMessagesByUserId(userId);
    }

    @Test
    public void testFetchMessagesByUserId_nullUserId() {
        chatService.fetchMessagesByUserId(null);
    }
}
