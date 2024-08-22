package org.abx.virturalpet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.util.UUID;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutablePetServiceDto.class)
@JsonDeserialize(as = ImmutablePetServiceDto.class)
public interface PetServiceDto extends Serializable {

    @Value.Default
    @JsonProperty("pet_id")
    default UUID getPetId() {
        return UUID.randomUUID();
    }

    @JsonProperty("pet_name")
    String getPetName();

    @JsonProperty("pet_type")
    PetTypeEnum getPetType();

    @JsonProperty("pet_age")
    int getPetAge();

    static ImmutablePetServiceDto.Builder builder() {
        return ImmutablePetServiceDto.builder();
    }
}