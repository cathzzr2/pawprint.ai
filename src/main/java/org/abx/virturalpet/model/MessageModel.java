package org.abx.virturalpet.model;

import java.sql.Timestamp;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "messages")
public class MessageModel {

    @Id
    private String id;

    private UUID threadId;

    private UUID userId;

    private String message;

    private Timestamp timestamp;

    public MessageModel() {
        this.id = UUID.randomUUID().toString();
    }

    public MessageModel(String id, String message) {
        this.id = id;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
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

    public static final class Builder {
        private String id;
        private UUID threadId;
        private UUID userId;
        private String message;
        private Timestamp timestamp;

        private Builder() {}

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withThreadId(UUID threadId) {
            this.threadId = threadId;
            return this;
        }

        public Builder withUserId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder withTimestamp(Timestamp timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public MessageModel build() {
            MessageModel messageModel = new MessageModel();
            messageModel.setId(id);
            messageModel.setThreadId(threadId);
            messageModel.setUserId(userId);
            messageModel.setMessage(message);
            messageModel.setTimestamp(timestamp);
            return messageModel;
        }
    }
}
