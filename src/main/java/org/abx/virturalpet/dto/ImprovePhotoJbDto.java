package org.abx.virturalpet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.sql.Timestamp;
import java.util.UUID;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableImprovePhotoJbDto.class)
@JsonDeserialize(as = ImmutableImprovePhotoJbDto.class)
public interface ImprovePhotoJbDto {
    @JsonProperty("job_id")
    @Value.Default
    default UUID getJobId() {
        return UUID.randomUUID();
    }

    @JsonProperty("user_id")
    @Value.Default
    default UUID getUserId() {
        return UUID.randomUUID();
    }

    @JsonProperty("photo_id")
    @Value.Default
    default UUID getPhotoId() {
        return UUID.randomUUID();
    }

    @JsonProperty("job_type")
    @Value.Default
    default String getJobType() {
        return "";
    }

    @JsonProperty("job_submission_time")
    @Value.Default
    default Timestamp getJobSubmissionTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    static ImmutableImprovePhotoJbDto.Builder builder() {
        return ImmutableImprovePhotoJbDto.builder();
    }
}
