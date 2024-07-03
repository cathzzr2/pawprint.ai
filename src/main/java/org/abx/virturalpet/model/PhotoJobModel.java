package org.abx.virturalpet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "photo_enhancement_jobs", schema = "virtural_pet_schema")
public class PhotoJobModel {

    @Id
    @Column(name = "job_id")
    private UUID jobId;

    @Column(name = "photo_id")
    private UUID photoId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "job_type")
    private String jobType;

    @Column(name = "job_submission_time")
    private Timestamp jobSubmissionTime;

    public PhotoJobModel() {}

    public PhotoJobModel(UUID jobId, UUID photoId, UUID userId, String jobType, Timestamp jobSubmissionTime) {
        this.jobId = jobId;
        this.photoId = photoId;
        this.userId = userId;
        this.jobType = jobType;
        this.jobSubmissionTime = jobSubmissionTime;
    }

    public UUID getJobId() {
        return jobId;
    }

    public void setJobId(UUID jobId) {
        this.jobId = jobId;
    }

    public UUID getPhotoId() {
        return photoId;
    }

    public void setPhotoId(UUID photoId) {
        this.photoId = photoId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public Timestamp getJobSubmissionTime() {
        return jobSubmissionTime;
    }

    public void setJobSubmissionTime(Timestamp jobSubmissionTime) {
        this.jobSubmissionTime = jobSubmissionTime;
    }
}
