package org.abx.virturalpet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableListObjectsRequestDto.class)
@JsonDeserialize(as = ImmutableListObjectsRequestDto.class)
public interface ListObjectsRequestDto {
    @JsonProperty("bucket_name")
    String getBuckName();

    @JsonProperty("prefix")
    String getPrefix();

    @JsonProperty("offset")
    @Value.Default
    default int getOffset() {
        return 0;
    }

    @JsonProperty("limit")
    @Value.Default
    default int getLimit() {
        return 0;
    }

    static ImmutableListObjectsRequestDto.Builder builder() {
        return ImmutableListObjectsRequestDto.builder();
    }
}
