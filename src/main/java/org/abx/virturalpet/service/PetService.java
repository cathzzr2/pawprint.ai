package org.abx.virturalpet.service;
import java.util.HashMap;
import java.util.Map;
import org.abx.virturalpet.dto.ImmutablePetServiceDto;
import org.abx.virturalpet.dto.PetServiceDto;
import org.springframework.stereotype.Service;

@Service
public class PetService {
    private static final Map<Integer, PetServiceDto> pets = new HashMap<>();

    public ImmutablePetServiceDto updatePet(Integer petID, PetServiceDto petServiceDto) {
        ImmutablePetServiceDto pet = (ImmutablePetServiceDto) pets.get(petID);
        if (pet == null) {
            return null;
        }
        ImmutablePetServiceDto.builder().from(pet).petAge(petServiceDto.getPetAge()).
                petName(petServiceDto.getPetName()).petType(petServiceDto.getPetType()).build();

        pets.put(petID, pet);
        return pet;
    }

    public PetServiceDto searchPetByID(Integer id) {
        return pets.get(id);
    }

    public boolean deletePetByID(Integer petId) {
        return pets.remove(petId) != null;
    }

    public PetServiceDto createPet(ImmutablePetServiceDto petServiceDto) {
        petServiceDto.withPetId(pets.size() + 1);
        pets.put(petServiceDto.getPetId(), petServiceDto);
        return petServiceDto;
    }
}
