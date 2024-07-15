package org.abx.virturalpet.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.abx.virturalpet.dto.ImmutableSendMessageDto;
import org.abx.virturalpet.dto.SendMessageDto;
import org.abx.virturalpet.model.MessageModel;
import org.abx.virturalpet.repository.MessageRepository;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private static final String USER_NOT_FOUND = "User not found";
    private static final String MESSAGE_SENT_SUCCESSFULLY = "Message Sent Successfully";
    private static final String MESSAGE_RECEIVED_SUCCESSFULLY = "Message Received Successfully";

    private final MessageRepository messageRepository;

    public ChatService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public SendMessageDto sendMessage(SendMessageDto sendMessageDto) {
        if (sendMessageDto.getMessageContent() == null
                || sendMessageDto.getMessageContent().isEmpty()) {
            return ImmutableSendMessageDto.builder()
                    .statusCode(1)
                    .status("No Message Content")
                    .build();
        }

        SendMessageDto newMessage = ImmutableSendMessageDto.builder()
                .from(sendMessageDto)
                .status("Sent")
                .statusCode(0)
                .build();

        MessageModel messageModel = fromMessageDto(newMessage);

        messageRepository.save(messageModel);

        return ImmutableSendMessageDto.builder()
                .status(MESSAGE_SENT_SUCCESSFULLY)
                .statusCode(0)
                .build();
    }

    public List<SendMessageDto> fetchMessagesByUserId(UUID userId) {
        if (userId == null) {
            List<SendMessageDto> errorResponse = new ArrayList<>();
            errorResponse.add(ImmutableSendMessageDto.builder()
                    .statusCode(1)
                    .status(USER_NOT_FOUND)
                    .build());
            return errorResponse;
        }

        List<MessageModel> userMessages = messageRepository.findByUserId(userId);
        List<SendMessageDto> userMessagesDto = new ArrayList<>();
        for (MessageModel messageModel : userMessages) {
            userMessagesDto.add(fromMessageModel(messageModel));
        }

        return userMessagesDto;
    }

    public List<SendMessageDto> fetchMessagesByThreadId(UUID threadId) {
        if (threadId == null) {
            List<SendMessageDto> errorResponse = new ArrayList<>();
            errorResponse.add(ImmutableSendMessageDto.builder()
                    .statusCode(1)
                    .status("Thread not found")
                    .build());
            return errorResponse;
        }

        List<MessageModel> threadMessages = messageRepository.findByThreadId(threadId);
        List<SendMessageDto> threadMessagesDto = new ArrayList<>();
        for (MessageModel messageModel : threadMessages) {
            threadMessagesDto.add(fromMessageModel(messageModel));
        }

        return threadMessagesDto;
    }

    public SendMessageDto fromMessageModel(MessageModel messageModel) {
        return ImmutableSendMessageDto.builder()
                .userId(messageModel.getUserId())
                .threadId(messageModel.getThreadId())
                .messageContent(messageModel.getMessage())
                .timestamp(messageModel.getTimestamp().toString())
                .build();
    }

    public MessageModel fromMessageDto(SendMessageDto sendMessageDto) {
        MessageModel messageModel = new MessageModel();
        messageModel.setUserId(sendMessageDto.getUserId());
        messageModel.setThreadId(sendMessageDto.getThreadId());
        messageModel.setMessage(sendMessageDto.getMessageContent());
        return messageModel;
    }
}
