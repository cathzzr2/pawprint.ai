package org.abx.virturalpet.model;

import java.util.UUID;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "job_progress")
public class JobProgress {
    @Id
    private ObjectId id; // MongoDB ID

    private UUID jobId;

    private String jobType;

    private String jobStatus;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId jobId) {
        this.id = id;
    }

    public UUID getJobId() {
        return jobId;
    }

    public void setJobId(UUID jobId) {
        this.jobId = jobId;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public static final class Builder {

        private ObjectId id;

        private UUID jobId;

        private String jobType;

        private String jobStatus;

        private Builder() {}

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder withId(ObjectId id) {
            this.id = id;
            return this;
        }

        public Builder withJobId(UUID jobId) {
            this.jobId = jobId;
            return this;
        }

        public Builder withJobType(String jobType) {
            this.jobType = jobType;
            return this;
        }

        public Builder withJobStatus(String jobStatus) {
            this.jobStatus = jobStatus;
            return this;
        }

        public JobProgress build() {
            JobProgress jobProgress = new JobProgress();
            jobProgress.setJobId(jobId);
            jobProgress.setJobType(jobType);
            jobProgress.setJobStatus(jobStatus);
            return jobProgress;
        }
    }
}
