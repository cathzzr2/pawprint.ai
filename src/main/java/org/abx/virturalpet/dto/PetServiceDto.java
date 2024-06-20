package org.abx.virturalpet.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutablePetServiceDto.class)
@JsonDeserialize(as = ImmutablePetServiceDto.class)
public interface PetServiceDto extends Serializable {

    @JsonProperty("pet_id")
    int getPetId();

    @JsonProperty("pet_name")
    String getPetName();

    @JsonProperty("petType")
    String getPetType();

    @JsonProperty("petAge")
    String getPetAge();

    static ImmutablePetServiceDto.Builder builder() {
        return ImmutablePetServiceDto.builder();
    }
}
