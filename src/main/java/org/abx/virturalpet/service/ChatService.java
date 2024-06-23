package org.abx.virturalpet.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.abx.virturalpet.dto.ImmutableSendMessageDto;
import org.abx.virturalpet.dto.SendMessageDto;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private static final String USER_NOT_FOUND = "User not found";
    private static final String MESSAGE_SENT_SUCCESSFULLY = "Message Sent Successfully";
    private static final String MESSAGE_RECEIVED_SUCCESSFULLY = "Message Received Successfully";

    private final List<SendMessageDto> messages = new ArrayList<>();

    public SendMessageDto sendMessage(SendMessageDto sendMessageDto) {
        if (sendMessageDto.getMessageContent() == null
                || sendMessageDto.getMessageContent().isEmpty()) {
            return ImmutableSendMessageDto.builder()
                    .statusCode(1)
                    .status("No Message Content")
                    .build();
        }

        int messageId = UUID.randomUUID().hashCode();
        SendMessageDto newMessage = ImmutableSendMessageDto.builder()
                .from(sendMessageDto)
                .messageId(messageId)
                .status("Sent")
                .statusCode(0)
                .build();

        messages.add(newMessage);

        return ImmutableSendMessageDto.builder()
                .messageId(messageId)
                .status(MESSAGE_SENT_SUCCESSFULLY)
                .statusCode(0)
                .build();
    }

    public List<SendMessageDto> fetchMessages(int userId) {
        if (userId <= 0) {
            List<SendMessageDto> errorResponse = new ArrayList<>();
            errorResponse.add(ImmutableSendMessageDto.builder()
                    .statusCode(1)
                    .status(USER_NOT_FOUND)
                    .build());
            return errorResponse;
        }

        List<SendMessageDto> userMessages = new ArrayList<>();
        for (SendMessageDto message : messages) {
            if (message.getUserId() == userId) {
                userMessages.add(message);
            }
        }

        return userMessages;
    }
}
