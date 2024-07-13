package org.abx.virturalpet.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import org.abx.virturalpet.model.MessageModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataMongoTest
public class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    private UUID threadId;

    private UUID userId;

    @BeforeEach
    public void before() {
        threadId = UUID.randomUUID();
        userId = UUID.randomUUID();
        MessageModel message1 = MessageModel.Builder.newBuilder()
                .withThreadId(threadId)
                .withMessage("Message 1")
                .withUserId(userId)
                .build();
        MessageModel message2 = MessageModel.Builder.newBuilder()
                .withThreadId(threadId)
                .withMessage("Message 2")
                .withUserId(userId)
                .build();
        messageRepository.saveAll(List.of(message1, message2));
    }

    @AfterEach
    public void cleanup() {
        messageRepository.deleteAll();
    }

    @Test
    public void testFindByThreadId() {
        List<MessageModel> messages = messageRepository.findByThreadId(threadId);
        Assertions.assertNotNull(messages);
        assertThat(messages).extracting("message").contains("Message 1", "Message 2");
    }

    @Test
    public void testFindByMessage() {
        MessageModel message = messageRepository.findByMessage("Message 1");
        Assertions.assertNotNull(message);
        assertThat(message.getMessage()).isEqualTo("Message 1");
    }

    @Test
    public void testFindByUserId() {
        List<MessageModel> messages = messageRepository.findByUserId(userId);
        Assertions.assertNotNull(messages);
        assertThat(messages).extracting("message").contains("Message 1", "Message 2");
    }
}
