package org.abx.virturalpet.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.abx.virturalpet.model.Thread;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThreadRepository extends JpaRepository<Thread, Long> {
    Optional<Thread> findByThreadId(UUID threadId);

    List<Thread> findByUserId(UUID userId);

    List<Thread> findAllByOrderByUpdatedAtDesc();

    List<Thread> findAllByOrderByCreatedAtAsc();

    Optional<Thread> findByThreadIdAndUserId(UUID threadId, UUID userId);

    List<Thread> findByUserIdAndUpdatedAtAfter(UUID userId, LocalDateTime updatedAt);
}
