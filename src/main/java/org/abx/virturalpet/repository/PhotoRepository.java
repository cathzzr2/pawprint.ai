package org.abx.virturalpet.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.abx.virturalpet.model.PhotoModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<PhotoModel, UUID> {
    List<PhotoModel> findByUserId(UUID userId);

    List<PhotoModel> findByUploadTime(java.sql.Timestamp lastActive);

    Optional<PhotoModel> findByS3Key(String s3Key);
}
