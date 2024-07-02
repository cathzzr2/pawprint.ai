package org.abx.virturalpet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "uploaded_original_photos", schema = "virtual_pet_schema")
public class PhotoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "photo_id", unique = true)
    private UUID photoId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "upload_time")
    private Timestamp uploadTime;

    @Column(name = "metadata", nullable = false)
    private String metadata;

    @Column(name = "s3_key", length = 255, nullable = false)
    private String s3Key;

    private PhotoModel(Builder builder) {
        setPhotoId(builder.photoId);
        setUserId(builder.userId);
        setUploadTime(builder.uploadTime);
        setMetadata(builder.metadata);
        setS3Key(builder.s3Key);
    }

    public PhotoModel() {}

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

    public Timestamp getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Timestamp uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getS3Key() {
        return s3Key;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    @Override
    public String toString() {
        return "Photo{" + "id=" + photoId + ", user=" + userId + ", upload time=" + uploadTime + ", s3 key=" + s3Key + '}';
    }

    public static final class Builder {

        private UUID photoId;
        private UUID userId;
        private Timestamp uploadTime;
        private String metadata;
        private String s3Key;

        private Builder() {}

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder withPhotoId(UUID val) {
            photoId = val;
            return this;
        }

        public Builder withUserId(UUID val) {
            userId = val;
            return this;
        }

        public Builder withUploadTime(Timestamp val) {
            uploadTime = val;
            return this;
        }

        public Builder withMetadata(String val) {
            metadata = val;
            return this;
        }

        public Builder withS3Key(String val) {
            s3Key = val;
            return this;
        }

        public PhotoModel build() {
            return new PhotoModel(this);
        }
    }
}
