package org.abx.virturalpet.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.abx.virturalpet.model.PhotoJobModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoJobRepository extends JpaRepository<PhotoJobModel, UUID> {
    PhotoJobModel findByJobId(UUID jobId);

    PhotoJobModel findByPhotoId(UUID photoId);

    Optional<List<PhotoJobModel>> findByUserId(UUID userId);

    Optional<List<PhotoJobModel>> findByJobType(String type);

    Optional<List<PhotoJobModel>> findByJobSubmissionTime(Timestamp jobSubmissionTime);
}
