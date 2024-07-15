package org.abx.virturalpet.service;

import java.util.UUID;
import org.abx.virturalpet.dto.SendMessageDto;
import org.abx.virturalpet.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

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

        Mockito.verify(messageRepository)
                .save(ArgumentMatchers.argThat(
                        savedModel -> savedModel.getUserId().equals(sendMessageDto.getUserId())
                                && savedModel.getThreadId().equals(sendMessageDto.getThreadId())
                                && savedModel.getMessage().equals(sendMessageDto.getMessageContent())));
    }

    @Test
    public void testFetchMessagesByUserId() {
        UUID userId = UUID.randomUUID();
        chatService.fetchMessagesByUserId(userId);
        Mockito.verify(messageRepository).findByUserId(userId);
    }

    @Test
    public void testFetchMessagesByUserId_nullUserId() {
        chatService.fetchMessagesByUserId(null);
    }

    @Test
    public void testFetchMessagesByThreadId() {
        UUID threadId = UUID.randomUUID();
        chatService.fetchMessagesByThreadId(threadId);
        Mockito.verify(messageRepository).findByThreadId(threadId);
    }

    @Test
    public void testFetchMessagesByThreadId_nullThreadId() {
        chatService.fetchMessagesByThreadId(null);
    }
}
