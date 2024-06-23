package org.abx.virturalpet.service;

import java.util.HashMap;
import java.util.Map;
import org.abx.virturalpet.dto.PetServiceDto;
import org.springframework.stereotype.Service;

@Service
public class PetService {
    private static final Map<Integer, PetServiceDto> pets = new HashMap<>();

    public PetServiceDto updatePet(int petID, PetServiceDto petServiceDto) {
        PetServiceDto pet = pets.get(petID);
        if (pet == null) {
            return null;
        }
        PetServiceDto updatedPet = PetServiceDto.builder()
                .from(pet)
                .petAge(petServiceDto.getPetAge())
                .petName(petServiceDto.getPetName())
                .petType(petServiceDto.getPetType())
                .build();

        pets.put(petID, updatedPet);
        return updatedPet;
    }

    public PetServiceDto searchPetByID(int id) {
        return pets.get(id);
    }

    public boolean deletePetByID(Integer petId) {
        return pets.remove(petId) != null;
    }

    public PetServiceDto createPet(PetServiceDto petServiceDto) {
        pets.put(petServiceDto.getPetId(), petServiceDto);
        return petServiceDto;
    }
}
