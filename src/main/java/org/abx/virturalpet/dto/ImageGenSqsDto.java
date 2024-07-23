package org.abx.virturalpet.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableImageGenSqsDto.class)
@JsonDeserialize(as = ImmutableImageGenSqsDto.class)
public interface ImageGenSqsDto {
    String getJobId();

    String photoId();

    String getJobType();
}
