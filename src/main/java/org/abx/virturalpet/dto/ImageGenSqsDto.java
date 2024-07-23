package org.abx.virturalpet.dto;

import org.immutables.value.Value;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImmutableImageGenSqsDto.class)
@JsonDeserialize(as = ImmutableImageGenSqsDto.class)
public interface ImageGenSqsDto {
    String getJobId();

    String photoId();

    String getJobType();
}
