package org.abx.virturalpet.repository;

import java.util.List;
import java.util.UUID;
import org.abx.virturalpet.model.JobProgress;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing PhotoMetadata documents in MongoDB.
 */
@Repository
public interface JobProgressRepository extends MongoRepository<JobProgress, ObjectId> {
    JobProgress findByJobId(UUID jobId);

    List<JobProgress> findByJobType(String jobType);

    List<JobProgress> findByJobStatus(String jobStatus);
}
