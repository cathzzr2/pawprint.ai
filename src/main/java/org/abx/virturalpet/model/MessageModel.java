package org.abx.virturalpet.model;

import jakarta.persistence.Entity;
import java.util.Date;
import java.util.UUID;
import org.springframework.data.mongodb.core.mapping.Document;

@Entity
@Document(collection = "messages")
public class MessageModel {

    @jakarta.persistence.Id
    private UUID id;

    private UUID threadId;

    private UUID userId;

    private String message;

    private Date timestamp;

    public MessageModel() {}

    public MessageModel(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public UUID getThreadId() {
        return threadId;
    }

    public void setThreadId(UUID threadId) {
        this.threadId = threadId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
