package org.abx.virturalpet.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.abx.virturalpet.dto.ImmutableSendMessageDto;
import org.abx.virturalpet.dto.SendMessageDto;
import org.abx.virturalpet.model.MessageModel;
import org.abx.virturalpet.repository.MessageRepository;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChatService {
    private static final String USER_NOT_FOUND = "User not found";
    private static final String MESSAGE_SENT_SUCCESSFULLY = "Message Sent Successfully";
    private static final String MESSAGE_RECEIVED_SUCCESSFULLY = "Message Received Successfully";

    private final MessageRepository messageRepository;
    private final GenerativeAiService generativeAiService;

    public ChatService(MessageRepository messageRepository, GenerativeAiService generativeAiService) {
        this.messageRepository = messageRepository;
        this.generativeAiService = generativeAiService;
    }

    public SendMessageDto sendMessage(SendMessageDto sendMessageDto) {
        if (sendMessageDto.getMessageContent() == null
                || sendMessageDto.getMessageContent().isEmpty()) {
            return ImmutableSendMessageDto.builder()
                    .statusCode(1)
                    .status("No Message Content")
                    .build();
        }

        // Call AI service to generate a response
        Flux<ChatResponse> aiResponseFlux =
                generativeAiService.generateStreamResponse(sendMessageDto.getMessageContent());

        // Block to get the last emitted ChatResponse
        ChatResponse lastChatResponse = aiResponseFlux.blockLast();
        if (lastChatResponse == null) {
            return ImmutableSendMessageDto.builder()
                    .statusCode(1)
                    .status("AI response failed")
                    .build();
        }

        String aiMessageContent = lastChatResponse.getResult().getOutput().toString();

        SendMessageDto newMessage = ImmutableSendMessageDto.builder()
                .from(sendMessageDto)
                .status("Sent")
                .aiMessageContent(aiMessageContent)
                .statusCode(0)
                .build();

        MessageModel messageModel = fromMessageDto(newMessage);

        messageRepository.save(messageModel);

        return ImmutableSendMessageDto.builder()
                .status(MESSAGE_SENT_SUCCESSFULLY)
                .statusCode(0)
                .aiMessageContent(aiMessageContent)
                .build();
    }

    public List<SendMessageDto> fetchMessagesByUserId(UUID userId, int pageNumber, int pageSize) {
        if (userId == null) {
            List<SendMessageDto> errorResponse = new ArrayList<>();
            errorResponse.add(ImmutableSendMessageDto.builder()
                    .statusCode(1)
                    .status(USER_NOT_FOUND)
                    .build());
            return errorResponse;
        }

        // Define the pageable object
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        // Fetch paginated user messages
        Page<MessageModel> userMessagesPage = messageRepository.findByUserId(userId, pageable);
        List<SendMessageDto> userMessagesDto = new ArrayList<>();
        for (MessageModel messageModel : userMessagesPage) {
            userMessagesDto.add(fromMessageModel(messageModel));
        }

        return userMessagesDto;
    }

    public List<SendMessageDto> fetchMessagesByThreadId(UUID threadId, int pageNumber, int pageSize) {
        if (threadId == null) {
            List<SendMessageDto> errorResponse = new ArrayList<>();
            errorResponse.add(ImmutableSendMessageDto.builder()
                    .statusCode(1)
                    .status("Thread not found")
                    .build());
            return errorResponse;
        }

        List<SendMessageDto> threadMessagesDto = new ArrayList<>();
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<MessageModel> threadMessagesPage = messageRepository.findByThreadId(threadId, pageable);

        for (MessageModel messageModel : threadMessagesPage) {
            threadMessagesDto.add(fromMessageModel(messageModel));
        }

        return threadMessagesDto;
    }

    public List<SendMessageDto> fetchAiMessagesByThreadId(UUID threadId) {
        if (threadId == null) {
            List<SendMessageDto> errorResponse = new ArrayList<>();
            errorResponse.add(ImmutableSendMessageDto.builder()
                    .statusCode(1)
                    .status("Thread not found")
                    .build());
            return errorResponse;
        }

        List<SendMessageDto> threadMessagesDto = new ArrayList<>();
        int pageSize = 10; // Set a page size
        Pageable pageable = Pageable.ofSize(pageSize);

        while (true) {
            Page<MessageModel> threadMessages = messageRepository.findByThreadId(threadId, pageable);

            // Filter and convert AI messages to SendMessageDto
            for (MessageModel messageModel : threadMessages) {
                if (messageModel.getAiMessageContent() != null
                        && !messageModel.getAiMessageContent().isEmpty()) {
                    threadMessagesDto.add(fromMessageModel(messageModel));
                }
            }

            // Check if the current page is the last page
            if (threadMessages.getContent().size() < pageSize) {
                break;
            }

            // Move to the next page
            pageable = pageable.next();
        }

        return threadMessagesDto;
    }

    public List<SendMessageDto> fetchUserAiMessagesByUserId(UUID userId) {
        if (userId == null) {
            List<SendMessageDto> errorResponse = new ArrayList<>();
            errorResponse.add(ImmutableSendMessageDto.builder()
                    .statusCode(1)
                    .status(USER_NOT_FOUND)
                    .build());
            return errorResponse;
        }

        // Define the pageable object
        Pageable pageable = Pageable.ofSize(10); // or use any page size as required

        // Fetch paginated user messages
        Page<MessageModel> userMessagesPage = messageRepository.findByUserId(userId, pageable);

        List<SendMessageDto> userMessagesDto = new ArrayList<>();
        for (MessageModel messageModel : userMessagesPage) {
            if (messageModel.getAiMessageContent() != null
                    && !messageModel.getAiMessageContent().isEmpty()) {
                userMessagesDto.add(fromMessageModel(messageModel));
            }
        }

        return userMessagesDto;
    }

    public SendMessageDto fromMessageModel(MessageModel messageModel) {
        return ImmutableSendMessageDto.builder()
                .userId(messageModel.getUserId())
                .threadId(messageModel.getThreadId())
                .messageContent(messageModel.getMessage())
                .timestamp(messageModel.getTimestamp().toString())
                .aiMessageContent(messageModel.getAiMessageContent())
                .build();
    }

    public MessageModel fromMessageDto(SendMessageDto sendMessageDto) {
        MessageModel messageModel = new MessageModel();
        messageModel.setUserId(sendMessageDto.getUserId());
        messageModel.setThreadId(sendMessageDto.getThreadId());
        messageModel.setMessage(sendMessageDto.getMessageContent());
        messageModel.setTimestamp(Timestamp.valueOf(sendMessageDto.getTimestamp()));
        messageModel.setAiMessageContent(sendMessageDto.getAiMessageContent());
        return messageModel;
    }
}
