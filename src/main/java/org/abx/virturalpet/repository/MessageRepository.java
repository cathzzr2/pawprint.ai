package org.abx.virturalpet.repository;

import java.util.List;
import java.util.UUID;
import org.abx.virturalpet.model.MessageModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends MongoRepository<MessageModel, String> {
    List<MessageModel> findByThreadId(UUID threadId);
    MessageModel findByMessage(String message);
    List<MessageModel> findByUserId(UUID userId);
}
