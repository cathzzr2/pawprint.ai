package org.abx.virturalpet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableUploadServiceDto.class)
@JsonDeserialize(as = ImmutableUploadServiceDto.class)
public interface UploadServiceDto {
    @JsonProperty("file_name")
    String getFileName();

    @JsonProperty("user_id")
    String getUserId();

    @JsonProperty("timestamp")
    String getTimestamp();

    @JsonProperty("s3_key")
    @Value.Default
    default String getS3Key() {
        return "";
    }

    @JsonProperty("metadata")
    String getMetadata();

    @JsonProperty("status_msg")
    @Value.Default
    default String getStatusMsg() {
        return "";
    }

    static ImmutableUploadServiceDto.Builder builder() {
        return ImmutableUploadServiceDto.builder();
    }
}
