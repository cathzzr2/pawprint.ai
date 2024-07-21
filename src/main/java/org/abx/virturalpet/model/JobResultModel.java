package org.abx.virturalpet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "photo_enhanced_results")
public class JobResultModel {

    @Id
    @Column(name = "result_id")
    private UUID resultId;

    @Column(name = "job_id")
    private UUID jobId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "generated_time")
    private Timestamp generatedTime;

    @Column(name = "s3_key")
    private String s3Key;

    public JobResultModel() {}

    public JobResultModel(UUID resultId, UUID jobId, UUID userId, Timestamp generatedTime, String s3Key) {
        this.resultId = resultId;
        this.jobId = jobId;
        this.userId = userId;
        this.generatedTime = generatedTime;
        this.s3Key = s3Key;
    }

    public UUID getResultId() {
        return resultId;
    }

    public void setResultId(UUID resultId) {
        this.resultId = resultId;
    }

    public UUID getJobId() {
        return jobId;
    }

    public void setJobId(UUID jobId) {
        this.jobId = jobId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Timestamp getGeneratedTime() {
        return generatedTime;
    }

    public void setGeneratedTime(Timestamp generatedTime) {
        this.generatedTime = generatedTime;
    }

    public String getS3Key() {
        return s3Key;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public static final class Builder {
        private UUID resultId;
        private UUID userId;
        private UUID jobId;
        private Timestamp generatedTime;
        private String s3Key;

        private Builder() {}

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder withResultId(UUID resultId) {
            this.resultId = resultId;
            return this;
        }

        public Builder withUserId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder withJobId(UUID jobId) {
            this.jobId = jobId;
            return this;
        }

        public Builder withGeneratedTime(Timestamp generatedTime) {
            this.generatedTime = generatedTime;
            return this;
        }

        public Builder withS3Key(String s3Key) {
            this.s3Key = s3Key;
            return this;
        }

        public JobResultModel build() {
            return new JobResultModel(resultId, userId, jobId, generatedTime, s3Key);
        }
    }
}
