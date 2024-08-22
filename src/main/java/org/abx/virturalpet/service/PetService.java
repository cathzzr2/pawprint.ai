package org.abx.virturalpet.service;

import java.util.Optional;
import java.util.UUID;
import org.abx.virturalpet.dto.ImmutablePetServiceDto;
import org.abx.virturalpet.dto.PetServiceDto;
import org.abx.virturalpet.dto.PetTypeEnum;
import org.abx.virturalpet.model.PetDoc;
import org.abx.virturalpet.repository.PetDocRepository;
import org.springframework.stereotype.Service;

@Service
public class PetService {

    private final PetDocRepository petDocRepository;

    public PetService(PetDocRepository petDocRepository) {
        this.petDocRepository = petDocRepository;
    }

    public PetServiceDto searchPetByID(UUID petId) {
        Optional<PetDoc> petDocOptional = petDocRepository.findById(petId);
        return petDocOptional.map(this::mapToDto).orElse(null);
    }

    public PetServiceDto updatePet(UUID petId, PetServiceDto petServiceDto) {
        Optional<PetDoc> petDocOptional = petDocRepository.findById(petId);
        if (petDocOptional.isPresent()) {
            PetDoc petDoc = petDocOptional.get();
            petDoc.setPetName(petServiceDto.getPetName());
            petDoc.setPetType(petServiceDto.getPetType().name());
            petDoc.setPetAge(petServiceDto.getPetAge());
            petDoc = petDocRepository.save(petDoc);
            return mapToDto(petDoc);
        }
        return null;
    }

    public boolean deletePetByID(UUID petId) {
        if (petDocRepository.existsById(petId)) {
            petDocRepository.deleteById(petId);
            return true;
        }
        return false;
    }

    public PetServiceDto createPet(PetServiceDto petServiceDto) {
        PetDoc petDoc = new PetDoc();
        petDoc.setPetName(petServiceDto.getPetName());
        petDoc.setPetType(petServiceDto.getPetType().name());
        petDoc.setPetAge(petServiceDto.getPetAge());
        petDoc = petDocRepository.save(petDoc);
        return mapToDto(petDoc);
    }

    // convert type PetDoc to PetServiceDto
    // to connect model/repo and dto
    private PetServiceDto mapToDto(PetDoc petDoc) {
        return ImmutablePetServiceDto.builder()
                .petId(petDoc.getPetId())
                .petName(petDoc.getPetName())
                .petType(PetTypeEnum.valueOf(petDoc.getPetType()))
                .petAge(petDoc.getPetAge())
                .build();
    }
}
