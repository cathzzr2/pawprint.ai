package org.abx.virturalpet.service;

import java.util.Collections;
import java.util.UUID;
import org.abx.virturalpet.dto.ImmutableSendMessageDto;
import org.abx.virturalpet.dto.SendMessageDto;
import org.abx.virturalpet.model.MessageModel;
import org.abx.virturalpet.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<MessageModel> mockPage = new PageImpl<>(Collections.emptyList());

        Mockito.when(messageRepository.findByUserId(Mockito.eq(userId), Mockito.eq(pageable)))
                .thenReturn(mockPage);

        chatService.fetchMessagesByUserId(userId, pageNumber, pageSize);
        org.mockito.Mockito.verify(messageRepository).findByUserId(Mockito.eq(userId), Mockito.eq(pageable));
    }

    @Test
    public void testFetchMessagesByUserId_nullUserId() {
        chatService.fetchMessagesByUserId(null, 0, 10);
    }

    @Test
    public void testFetchMessagesByThreadId() {
        UUID threadId = UUID.randomUUID();
        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<MessageModel> mockPage = new PageImpl<>(Collections.emptyList());
        Mockito.when(messageRepository.findByThreadId(Mockito.eq(threadId), Mockito.eq(pageable)))
                .thenReturn(mockPage);
        chatService.fetchMessagesByThreadId(threadId, pageNumber, pageSize);
        org.mockito.Mockito.verify(messageRepository).findByThreadId(Mockito.eq(threadId), Mockito.eq(pageable));
    }

    @Test
    public void testFetchMessagesByThreadId_nullThreadId() {
        chatService.fetchMessagesByThreadId(null, 0, 10);
    }

    @Test
    public void testFetchAiMessagesByThreadId() {
        UUID threadId = UUID.randomUUID();
        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<MessageModel> mockPage = new PageImpl<>(Collections.emptyList());
        Mockito.when(messageRepository.findByThreadId(Mockito.eq(threadId), Mockito.eq(pageable)))
                .thenReturn(mockPage);
        chatService.fetchAiMessagesByThreadId(threadId, pageNumber, pageSize);
        org.mockito.Mockito.verify(messageRepository).findByThreadId(Mockito.eq(threadId), Mockito.eq(pageable));
    }

    @Test
    public void testFetchAiMessagesByThreadId_nullThreadId() {
        chatService.fetchAiMessagesByThreadId(null, 0, 10);
    }

    @Test
    public void testFetchAiMessagesByUserId() {
        UUID userId = UUID.randomUUID();
        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<MessageModel> mockPage = new PageImpl<>(Collections.emptyList());
        Mockito.when(messageRepository.findByUserId(Mockito.eq(userId), Mockito.eq(pageable)))
                .thenReturn(mockPage);
        chatService.fetchAiMessagesByUserId(userId, pageNumber, pageSize);
        org.mockito.Mockito.verify(messageRepository).findByUserId(Mockito.eq(userId), Mockito.eq(pageable));
    }

    @Test
    public void testFetchAiMessagesByUserId_nullUserId() {
        chatService.fetchAiMessagesByUserId(null, 0, 10);
    }
}
