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
        SendMessageDto sendMessageDto = ImmutableSendMessageDto.builder()
                .userId(UUID.randomUUID())
                .threadId(UUID.randomUUID())
                .messageContent("Hello, how are you?")
                .build();

        SendMessageDto responseDto = ImmutableSendMessageDto.builder()
                .status("Sent")
                .statusCode(0)
                .build();

        when(chatService.sendMessage(sendMessageDto)).thenReturn(responseDto);

        String requestJsonPayload = "{\n"
                + "\"user_id\": 1,\n"
                + "\"thread_id\": 1,\n"
                + "\"message_content\": \"Hello, how are you?\"\n"
                + "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/messages/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJsonPayload))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message_id").value(456))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("Sent"));
    }

    @Test
    public void testFetchMessages_ByUserId_returnOk() throws Exception {
        UUID userId = UUID.randomUUID();
        SendMessageDto message1 = ImmutableSendMessageDto.builder()
                .userId(userId)
                .threadId(UUID.randomUUID())
                .messageContent("Hello, how are you?")
                .status("Received")
                .build();

        List<SendMessageDto> messages = List.of(message1);

        when(chatService.fetchMessagesByUserId(userId)).thenReturn(messages);

        mockMvc.perform(MockMvcRequestBuilders.get("/messages/receive/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message_id").value(456))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$[0].message_content").value("Hello, how are you?"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value("Received"));
    }

    @Test
    public void testFetchMessages_ByUserId_returnNotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        when(chatService.fetchMessagesByUserId(userId))
                .thenReturn(List.of(ImmutableSendMessageDto.builder()
                        .statusCode(1)
                        .status("User not found")
                        .build()));

        mockMvc.perform(MockMvcRequestBuilders.get("/messages/receive/{userId}").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
