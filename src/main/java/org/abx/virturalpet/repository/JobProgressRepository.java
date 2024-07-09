package org.abx.virturalpet.repository;

import org.abx.virturalpet.model.JobProgress;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for accessing PhotoMetadata documents in MongoDB.
 */
public interface JobProgressRepository extends MongoRepository<JobProgress, ObjectId> {
    JobProgress findByJobId(UUID jobId);

    List<JobProgress> findByJobType(String jobType);

    List<JobProgress> findByJobStatus(String jobStatus);
}