package org.abx.virturalpet.controller;

import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.abx.virturalpet.dto.ImmutableSendMessageDto;
import org.abx.virturalpet.dto.SendMessageDto;
import org.abx.virturalpet.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(ChatServiceController.class)
public class ChatServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @Test
    public void testSendMessage_returnCreated() throws Exception {
        SendMessageDto message = ImmutableSendMessageDto.builder()
                .userId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .threadId(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"))
                .messageContent("Hello, how are you?")
                .timestamp("2021-09-01T12:00:00")
                .status("Sent")
                .statusCode(0)
                .build();

        when(chatService.sendMessage(message)).thenReturn(message);

        String messageContent = "{\n"
                + "\"user_id\": \"123e4567-e89b-12d3-a456-426614174000\",\n"
                + "\"thread_id\": \"123e4567-e89b-12d3-a456-426614174001\",\n"
                + "\"message_content\": \"Hello, how are you?\",\n"
                + "\"timestamp\": \"2021-09-01T12:00:00\",\n"
                + "\"status\": \"Sent\",\n"
                + "\"status_code\": 0\n"
                + "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/messages/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageContent))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.user_id").value("123e4567-e89b-12d3-a456-426614174000"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.thread_id").value("123e4567-e89b-12d3-a456-426614174001"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message_content").value("Hello, how are you?"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").value("2021-09-01T12:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("Sent"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status_code").value(0));
    }

    @Test
    public void testFetchMessages_ByUserId_returnOk() throws Exception {
        UUID userId = UUID.randomUUID();
        when(chatService.fetchMessagesByUserId(userId))
                .thenReturn(List.of(ImmutableSendMessageDto.builder()
                        .userId(userId)
                        .threadId(UUID.randomUUID())
                        .messageContent("Hello, how are you?")
                        .timestamp("2021-09-01T12:00:00")
                        .statusCode(0)
                        .status("Success")
                        .build()));

        mockMvc.perform(MockMvcRequestBuilders.get("/messages/receive/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].user_id").value(userId.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].thread_id").isNotEmpty())
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$[0].message_content").value("Hello, how are you?"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].timestamp").value("2021-09-01T12:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status_code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value("Success"));
    }

    @Test
    public void testFetchMessages_ByUserId_returnNotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        when(chatService.fetchMessagesByUserId(userId))
                .thenReturn(List.of(ImmutableSendMessageDto.builder()
                        .userId(userId)
                        .threadId(UUID.randomUUID())
                        .messageContent("Hello, how are you?")
                        .timestamp("2021-09-01T12:00:00")
                        .statusCode(1)
                        .status("Failed")
                        .build()));

        mockMvc.perform(MockMvcRequestBuilders.get("/messages/receive/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].user_id").value(userId.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].thread_id").isNotEmpty())
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$[0].message_content").value("Hello, how are you?"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].timestamp").value("2021-09-01T12:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status_code").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value("Failed"));
    }

    @Test
    public void testFetchMessages_ByThreadId_returnOk() throws Exception {
        UUID threadId = UUID.randomUUID();
        when(chatService.fetchMessagesByThreadId(threadId))
                .thenReturn(List.of(ImmutableSendMessageDto.builder()
                        .userId(UUID.randomUUID())
                        .threadId(threadId)
                        .messageContent("Hello, how are you?")
                        .timestamp("2021-09-01T12:00:00")
                        .statusCode(0)
                        .status("Success")
                        .build()));

        mockMvc.perform(MockMvcRequestBuilders.get("/messages/receive/thread/{threadId}", threadId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].user_id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].thread_id").value(threadId.toString()))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$[0].message_content").value("Hello, how are you?"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].timestamp").value("2021-09-01T12:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status_code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value("Success"));
    }

    @Test
    public void testFetchMessages_ByThreadId_returnNotFound() throws Exception {
        UUID threadId = UUID.randomUUID();
        when(chatService.fetchMessagesByThreadId(threadId))
                .thenReturn(List.of(ImmutableSendMessageDto.builder()
                        .userId(UUID.randomUUID())
                        .threadId(threadId)
                        .messageContent("Hello, how are you?")
                        .timestamp("2021-09-01T12:00:00")
                        .statusCode(1)
                        .status("Failed")
                        .build()));

        mockMvc.perform(MockMvcRequestBuilders.get("/messages/receive/thread/{threadId}", threadId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].user_id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].thread_id").value(threadId.toString()))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$[0].message_content").value("Hello, how are you?"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].timestamp").value("2021-09-01T12:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status_code").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value("Failed"));
    }
}
