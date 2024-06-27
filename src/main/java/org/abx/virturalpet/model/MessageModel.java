package org.abx.virturalpet.model;

import jakarta.persistence.Entity;
import org.immutables.value.internal.$processor$.meta.$MongoMirrors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Entity
@Document(collection = "messages")
public class MessageModel {

    @jakarta.persistence.Id
    private UUID id;

    private UUID thread_id;

    private UUID user_id;

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

    public UUID getThread_id() {
        return thread_id;
    }

    public void setThread_id(UUID thread_id) {
        this.thread_id = thread_id;
    }

    public UUID getUser_id() {
        return user_id;
    }

    public void setUser_id(UUID user_id) {
        this.user_id = user_id;
    }
}