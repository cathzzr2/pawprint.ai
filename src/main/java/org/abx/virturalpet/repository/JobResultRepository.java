package org.abx.virturalpet.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.abx.virturalpet.model.JobResultModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobResultRepository extends JpaRepository<JobResultModel, UUID> {
    JobResultModel findByResultId(UUID resultId);

    JobResultModel findByJobId(UUID jobId);

    Optional<List<JobResultModel>> findByGeneratedTime(Timestamp generatedTime);

    Optional<List<JobResultModel>> findByS3Key(String s3Key);
}
