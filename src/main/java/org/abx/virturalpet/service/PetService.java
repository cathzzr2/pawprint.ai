package org.abx.virturalpet.service;

import java.util.Optional;
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

    public PetServiceDto searchPetByID(int id) {
        Optional<PetDoc> petDocOptional = petDocRepository.findById((long) id);
        return petDocOptional.map(this::mapToDto).orElse(null);
    }

    public PetServiceDto updatePet(int petID, PetServiceDto petServiceDto) {
        Optional<PetDoc> petDocOptional = petDocRepository.findById((long) petID);
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

    public boolean deletePetByID(Integer petId) {
        if (petDocRepository.existsById((long) petId)) {
            petDocRepository.deleteById((long) petId);
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
                .petId(petDoc.getPetId().intValue())
                .petName(petDoc.getPetName())
                .petType(PetTypeEnum.valueOf(petDoc.getPetType()))
                .petAge(petDoc.getPetAge())
                .build();
    }
}
