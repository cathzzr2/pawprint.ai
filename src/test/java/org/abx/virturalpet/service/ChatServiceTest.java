package org.abx.virturalpet.service;

import java.util.UUID;
import org.abx.virturalpet.dto.ImmutableSendMessageDto;
import org.abx.virturalpet.dto.SendMessageDto;
import org.abx.virturalpet.model.MessageModel;
import org.abx.virturalpet.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import reactor.core.publisher.Flux;

public class ChatServiceTest {

    private MessageRepository messageRepository;
    private GenerativeAiService generativeAiService;
    private ChatService chatService;

    @BeforeEach
    void before() {
        messageRepository = org.mockito.Mockito.mock(MessageRepository.class);
        generativeAiService = org.mockito.Mockito.mock(GenerativeAiService.class);
        chatService = new ChatService(messageRepository, generativeAiService);
    }

    @Test
    void testSendMessage() {
        String messageContent = "Hello, AI!";
        SendMessageDto sendMessageDto = ImmutableSendMessageDto.builder()
                .userId(UUID.randomUUID())
                .threadId(UUID.randomUUID())
                .messageContent(messageContent)
                .build();

        // Mock AssistantMessage with a non-null result
        AssistantMessage assistantMessage = org.mockito.Mockito.mock(AssistantMessage.class);
        org.mockito.Mockito.when(assistantMessage.toString()).thenReturn("I'm fine, thank you!");

        // Mock Generation to return the mocked AssistantMessage
        Generation generation = org.mockito.Mockito.mock(Generation.class);
        org.mockito.Mockito.when(generation.getOutput()).thenReturn(assistantMessage);

        // Mock ChatResponse to return the mocked Generation
        ChatResponse chatResponse = org.mockito.Mockito.mock(ChatResponse.class);
        org.mockito.Mockito.when(chatResponse.getResult()).thenReturn(generation);

        // Return the mocked ChatResponse from the Flux
        org.mockito.Mockito.when(generativeAiService.generateStreamResponse(messageContent))
                .thenReturn(Flux.just(chatResponse));

        SendMessageDto response = chatService.sendMessage(sendMessageDto);

        // Validate response
        org.junit.jupiter.api.Assertions.assertEquals("Message Sent Successfully", response.getStatus());
        org.junit.jupiter.api.Assertions.assertEquals(0, response.getStatusCode());
        org.junit.jupiter.api.Assertions.assertEquals("I'm fine, thank you!", response.getAiMessageContent());
        org.mockito.Mockito.verify(messageRepository).save(org.mockito.ArgumentMatchers.any(MessageModel.class));
    }

    @Test
    public void testFetchMessagesByUserId() {
        UUID userId = UUID.randomUUID();
        chatService.fetchMessagesByUserId(userId);
        org.mockito.Mockito.verify(messageRepository).findByUserId(userId);
    }

    @Test
    public void testFetchMessagesByUserId_nullUserId() {
        chatService.fetchMessagesByUserId(null);
    }

    @Test
    public void testFetchMessagesByThreadId() {
        UUID threadId = UUID.randomUUID();
        chatService.fetchMessagesByThreadId(threadId);
        org.mockito.Mockito.verify(messageRepository).findByThreadId(threadId);
    }

    @Test
    public void testFetchMessagesByThreadId_nullThreadId() {
        chatService.fetchMessagesByThreadId(null);
    }
}
