package org.abx.virturalpet.repository;

import org.abx.virturalpet.model.MessageModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends MongoRepository<MessageModel, String>{
    List<MessageModel> findByThreadId(UUID threadId);
    MessageModel findByMessage(String message);
}