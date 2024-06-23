package org.abx.virturalpet.controller;

import static org.mockito.Mockito.when;

import java.util.List;
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
                .userId(1)
                .threadId(1)
                .messageContent("Hello, how are you?")
                .build();

        SendMessageDto responseDto = ImmutableSendMessageDto.builder()
                .messageId(456)
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
    public void testFetchMessages_returnOk() throws Exception {
        SendMessageDto message1 = ImmutableSendMessageDto.builder()
                .userId(1)
                .threadId(1)
                .messageContent("Hello, how are you?")
                .messageId(456)
                .status("Received")
                .build();

        List<SendMessageDto> messages = List.of(message1);

        when(chatService.fetchMessages(1)).thenReturn(messages);

        mockMvc.perform(MockMvcRequestBuilders.get("/messages/receive/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message_id").value(456))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$[0].message_content").value("Hello, how are you?"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value("Received"));
    }

    @Test
    public void testFetchMessages_returnNotFound() throws Exception {
        when(chatService.fetchMessages(0))
                .thenReturn(List.of(ImmutableSendMessageDto.builder()
                        .statusCode(1)
                        .status("User not found")
                        .build()));

        mockMvc.perform(MockMvcRequestBuilders.get("/messages/receive/0").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
