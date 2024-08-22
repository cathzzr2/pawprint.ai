package org.abx.virturalpet.repository;

import java.util.List;
import java.util.UUID;
import org.abx.virturalpet.model.MessageModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends MongoRepository<MessageModel, String> {
    // Overloaded methods
    List<MessageModel> findByThreadId(UUID threadId);

    Page<MessageModel> findByThreadId(UUID threadId, Pageable pageable);

    // Non-overloaded methods
    MessageModel findByMessage(String message);

    List<MessageModel> findByUserId(UUID userId);

    Page<MessageModel> findByUserId(UUID userId, Pageable pageable);
}
