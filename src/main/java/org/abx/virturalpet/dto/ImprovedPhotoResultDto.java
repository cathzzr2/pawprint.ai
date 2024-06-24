package org.abx.virturalpet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableImprovedPhotoResultDto.class)
@JsonDeserialize(as = ImmutableImprovedPhotoResultDto.class)
public interface ImprovedPhotoResultDto {

    @JsonProperty("improved_photo_url")
    @Value.Default
    default String getImprovedPhotoUrl() {
        return "";
    }

    static ImmutableImprovedPhotoResultDto.Builder builder() {
        return ImmutableImprovedPhotoResultDto.builder();
    }
}
