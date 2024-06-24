package org.abx.virturalpet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableImprovePhotoJbDto.class)
@JsonDeserialize(as = ImmutableImprovePhotoJbDto.class)
public interface ImprovePhotoJbDto {
    @JsonProperty("queue_jb_id")
    @Value.Default
    default String getImprovePhotoJbId() {
        return "";
    }

    @JsonProperty("photo_file")
    @Value.Default
    default String getPhotoFile() {
        return "";
    }

    static ImmutableImprovePhotoJbDto.Builder builder() {
        return ImmutableImprovePhotoJbDto.builder();
    }
}
