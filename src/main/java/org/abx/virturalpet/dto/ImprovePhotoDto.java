package org.abx.virturalpet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableImprovePhotoDto.class)
@JsonDeserialize(as = ImmutableImprovePhotoDto.class)
// @JsonIgnoreProperties(ignoreUnknown = true)
public interface ImprovePhotoDto {
    @JsonProperty("status_code")
    @Value.Default
    default int getStatusCode() {
        return 0;
    }

    @JsonProperty("status_msg")
    @Value.Default
    default String getStatusMsg() {
        return "";
    }

    @JsonProperty("improved_photo_id")
    @Value.Default
    default String getImprovedPhotoId() {
        return "";
    }

    @JsonProperty("photo_file")
    @Value.Default
    default String getPhotoFile() {
        return "";
    }

    @JsonProperty("improved_photo_url")
    @Value.Default
    default String getImprovedPhotoUrl() {
        return "";
    }

    static ImmutableImprovePhotoDto.Builder builder() {
        return ImmutableImprovePhotoDto.builder();
    }
}
