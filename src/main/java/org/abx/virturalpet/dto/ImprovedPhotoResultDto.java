package org.abx.virturalpet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.sql.Timestamp;
import java.util.UUID;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableImprovedPhotoResultDto.class)
@JsonDeserialize(as = ImmutableImprovedPhotoResultDto.class)
public interface ImprovedPhotoResultDto {
    @JsonProperty("result_id")
    @Value.Default
    default UUID getResultId() {
        return UUID.randomUUID();
    }

    @JsonProperty("user_id")
    @Value.Default
    default UUID getUserId() {
        return UUID.randomUUID();
    }

    @JsonProperty("job_id")
    @Value.Default
    default UUID getJobId() {
        return UUID.randomUUID();
    }

    @JsonProperty("generated_time")
    @Value.Default
    default Timestamp getGeneratedTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    @JsonProperty("s3_key")
    @Value.Default
    default String getS3Key() {
        return "";
    }

    static ImmutableImprovedPhotoResultDto.Builder builder() {
        return ImmutableImprovedPhotoResultDto.builder();
    }
}
