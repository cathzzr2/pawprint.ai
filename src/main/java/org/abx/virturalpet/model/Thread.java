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


  public Thread(UUID threadId, UUID userId, LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.threadId = threadId;
    this.userId = userId;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;

  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public static final class ThreadBuilder {
    private Long id;
    private UUID threadId;
    private UUID userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private ThreadBuilder() {
    }

    public static ThreadBuilder aThread() {
      return new ThreadBuilder();
    }

    public ThreadBuilder withId(Long id) {
      this.id = id;
      return this;
    }

    public ThreadBuilder withThreadId(UUID threadId) {
      this.threadId = threadId;
      return this;
    }

    public ThreadBuilder withUserId(UUID userId) {
      this.userId = userId;
      return this;
    }

    public ThreadBuilder withCreatedAt(LocalDateTime createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public ThreadBuilder withUpdatedAt(LocalDateTime updatedAt) {
      this.updatedAt = updatedAt;
      return this;
    }

    public Thread build() {
      Thread thread = new Thread();
      thread.setId(id);
      thread.setThreadId(threadId);
      thread.setUserId(userId);
      thread.setCreatedAt(createdAt);
      thread.setUpdatedAt(updatedAt);
      return thread;
    }
  }
}