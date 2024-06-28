package org.abx.virturalpet.repository;

import java.util.List;
import java.util.UUID;

import org.abx.virturalpet.model.MessageModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(SpringExtension.class)
@DataMongoTest
public class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Test
    public void testFindByThreadId() {
        UUID threadId = UUID.randomUUID();
        MessageModel message1 = new MessageModel();
        message1.setThreadId(threadId);
        message1.setMessage("Message 1");
        message1.setUserId(UUID.randomUUID());

        MessageModel message2 = new MessageModel();
        message2.setThreadId(threadId);
        message2.setMessage("Message 2");
        message2.setUserId(UUID.randomUUID());

        messageRepository.save(message1);
        messageRepository.save(message2);

        List<MessageModel> messages = messageRepository.findByThreadId(threadId);

        assertThat(messages).hasSize(2);
        assertThat(messages).extracting("message").contains("Message 1", "Message 2");
    }

    @Test
    public void testFindByMessage() {
        MessageModel message1 = new MessageModel();
        message1.setThreadId(UUID.randomUUID());
        message1.setMessage("Message 1");
        message1.setUserId(UUID.randomUUID());

        MessageModel message2 = new MessageModel();
        message2.setThreadId(UUID.randomUUID());
        message2.setMessage("Message 2");
        message2.setUserId(UUID.randomUUID());

        messageRepository.save(message1);
        messageRepository.save(message2);

        MessageModel message = messageRepository.findByMessage("Message 1");

        assertThat(message).isNotNull();
        assertThat(message.getMessage()).isEqualTo("Message 1");

    }

    @Test
    public void testFindByUserId() {
        UUID userId = UUID.randomUUID();
        MessageModel message1 = new MessageModel();
        message1.setThreadId(UUID.randomUUID());
        message1.setMessage("Message 1");
        message1.setUserId(userId);

        MessageModel message2 = new MessageModel();
        message2.setThreadId(UUID.randomUUID());
        message2.setMessage("Message 2");
        message2.setUserId(userId);

        messageRepository.save(message1);
        messageRepository.save(message2);

        List<MessageModel> messages = messageRepository.findByUserId(userId);

        assertThat(messages).hasSize(2);
        assertThat(messages).extracting("message").contains("Message 1", "Message 2");

    }


}
