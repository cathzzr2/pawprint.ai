package org.abx.virturalpet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutablePhotoGenerationDto.class)
@JsonDeserialize(as = ImmutablePhotoGenerationDto.class)
public interface PhotoGenerationDto extends Serializable {
    @JsonProperty("image_data")
    @Value.Default
    default String getImageData() {
        return "";
    }

    @JsonProperty("image_id")
    @Value.Default
    default String getImageId() {
        return "";
    }

    @JsonProperty("job_id")
    @Value.Default
    default String getJobId() {
        return "";
    }

    @JsonProperty("jb_status")
    @Value.Default
    default JobStatus getStatus() {
        return JobStatus.FAILED;
    }

    @JsonProperty("user_id")
    @Value.Default
    default String getUserId() {
        return "";
    }

    @JsonProperty("job_type")
    @Value.Default
    default JobType getJobType() {
        return JobType.ENHANCE;
    }

    static ImmutablePhotoGenerationDto.Builder builder() {
        return ImmutablePhotoGenerationDto.builder();
    }
}
