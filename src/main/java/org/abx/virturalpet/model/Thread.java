package org.abx.virturalpet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "thread", schema = "virtual_pet_schema")
public class Thread {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "thread_id", nullable = false, unique = true)
    private UUID threadId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Thread() {}

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getThreadId() {
        return threadId;
    }

    public void setThreadId(UUID threadId) {
        this.threadId = threadId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static final class ThreadBuilder {

        private Thread thread;

        private ThreadBuilder() {
            thread = new Thread();
        }

        public static ThreadBuilder aThread() {
            return new ThreadBuilder();
        }

        public ThreadBuilder withId(Long id) {
            thread.setId(id);
            return this;
        }

        public ThreadBuilder withThreadId(UUID threadId) {
            thread.setThreadId(threadId);
            return this;
        }

        public ThreadBuilder withUserId(UUID userId) {
            thread.setUserId(userId);
            return this;
        }

        public ThreadBuilder withCreatedAt(LocalDateTime createdAt) {
            thread.setCreatedAt(createdAt);
            return this;
        }

        public ThreadBuilder withUpdatedAt(LocalDateTime updatedAt) {
            thread.setUpdatedAt(updatedAt);
            return this;
        }

        public Thread build() {
            return thread;
        }
    }
}
