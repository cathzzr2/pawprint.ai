package org.abx.virturalpet.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.abx.virturalpet.model.Thread;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ThreadRepositoryTest {
    @Mock
    private ThreadRepository threadRepository;

    private UUID threadId;
    private UUID userId;
    private Thread thread;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    @BeforeEach
    public void init() {
        threadId = UUID.randomUUID();
        userId = UUID.randomUUID();
        updatedAt = LocalDateTime.now();
        createdAt = LocalDateTime.now().minusDays(1);
        thread = new Thread();
        thread.setThreadId(threadId);
        thread.setUserId(userId);
        thread.setUpdatedAt(updatedAt);
        thread.setCreatedAt(createdAt);
    }

    @Test
    public void testFindByThreadId() {
        Mockito.when(threadRepository.findByThreadId(ArgumentMatchers.any(UUID.class)))
                .thenReturn(Optional.of(thread));

        Optional<Thread> foundThread = threadRepository.findByThreadId(threadId);

        Assertions.assertTrue(foundThread.isPresent());
        Assertions.assertEquals(threadId, foundThread.get().getThreadId());
    }

    @Test
    public void testFindByUserId() {
        Mockito.when(threadRepository.findByUserId(ArgumentMatchers.any(UUID.class)))
                .thenReturn(List.of(thread));

        List<Thread> foundThreads = threadRepository.findByUserId(userId);

        Assertions.assertFalse(foundThreads.isEmpty());
        Assertions.assertEquals(userId, foundThreads.get(0).getUserId());
    }

    @Test
    public void testFindAllByOrderByUpdatedAtDesc() {
        Mockito.when(threadRepository.findAllByOrderByUpdatedAtDesc()).thenReturn(List.of(thread));

        List<Thread> foundThreads = threadRepository.findAllByOrderByUpdatedAtDesc();

        Assertions.assertFalse(foundThreads.isEmpty());
        Assertions.assertEquals(updatedAt, foundThreads.get(0).getUpdatedAt());
    }

    @Test
    public void testFindAllByOrderByCreatedAtAsc() {
        Mockito.when(threadRepository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(thread));

        List<Thread> foundThreads = threadRepository.findAllByOrderByCreatedAtAsc();

        Assertions.assertFalse(foundThreads.isEmpty());
        Assertions.assertEquals(createdAt, foundThreads.get(0).getCreatedAt());
    }

    @Test
    public void testFindByThreadIdAndUserId() {
        Mockito.when(threadRepository.findByThreadIdAndUserId(
                        ArgumentMatchers.any(UUID.class), ArgumentMatchers.any(UUID.class)))
                .thenReturn(Optional.of(thread));

        Optional<Thread> foundThread = threadRepository.findByThreadIdAndUserId(threadId, userId);

        Assertions.assertTrue(foundThread.isPresent());
        Assertions.assertEquals(threadId, foundThread.get().getThreadId());
        Assertions.assertEquals(userId, foundThread.get().getUserId());
    }

    @Test
    public void testFindByUserIdAndUpdatedAtAfter() {
        Mockito.when(threadRepository.findByUserIdAndUpdatedAtAfter(
                        ArgumentMatchers.any(UUID.class), ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(List.of(thread));

        List<Thread> foundThreads = threadRepository.findByUserIdAndUpdatedAtAfter(userId, updatedAt.minusDays(1));

        Assertions.assertFalse(foundThreads.isEmpty());
        Assertions.assertEquals(userId, foundThreads.get(0).getUserId());
    }
}
