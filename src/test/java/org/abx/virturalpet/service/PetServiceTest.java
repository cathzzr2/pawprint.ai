package org.abx.virturalpet.service;

import java.util.Optional;
import java.util.UUID;
import org.abx.virturalpet.dto.ImmutablePetServiceDto;
import org.abx.virturalpet.dto.PetServiceDto;
import org.abx.virturalpet.dto.PetTypeEnum;
import org.abx.virturalpet.model.PetDoc;
import org.abx.virturalpet.repository.PetDocRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class PetServiceTest {

    @Mock
    private PetDocRepository petDocRepository;

    @InjectMocks
    private PetService petService;

    private UUID samplePetId = UUID.randomUUID();

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSearchPetByID() {

        PetDoc petDoc = new PetDoc.Builder()
                .setPetId(samplePetId) // cast from int to long
                .setPetName("Rover")
                .setPetType("DOG")
                .setPetAge(5)
                .build();
        Mockito.when(petDocRepository.findById(samplePetId)).thenReturn(Optional.of(petDoc));

        PetServiceDto petServiceDto = petService.searchPetByID(samplePetId);

        Assertions.assertNotNull(petServiceDto);
        Assertions.assertEquals(samplePetId, petServiceDto.getPetId());
        Assertions.assertEquals("Rover", petServiceDto.getPetName());
        Assertions.assertEquals(PetTypeEnum.DOG, petServiceDto.getPetType());
        Assertions.assertEquals(5, petServiceDto.getPetAge());
    }

    @Test
    public void testSearchPetByIdNotFound() {
        Mockito.when(petDocRepository.findById(samplePetId)).thenReturn(Optional.empty());

        PetServiceDto petServiceDto = petService.searchPetByID(samplePetId);

        Assertions.assertNull(petServiceDto);
    }

    @Test
    public void testCreatePet() {
        PetServiceDto petServiceDto = ImmutablePetServiceDto.builder()
                .petName("Rover")
                .petType(PetTypeEnum.DOG)
                .petAge(1)
                .build();

        PetDoc petDoc = new PetDoc.Builder()
                .setPetId(samplePetId)
                .setPetName("Rover")
                .setPetType("DOG")
                .setPetAge(1)
                .build();

        // mockito any matcher for any object
        Mockito.when(petDocRepository.save(Mockito.any(PetDoc.class))).thenReturn(petDoc);

        PetServiceDto createdPetServiceDto = petService.createPet(petServiceDto);

        Assertions.assertNotNull(createdPetServiceDto);
        Assertions.assertEquals(samplePetId, createdPetServiceDto.getPetId());
        Assertions.assertEquals("Rover", createdPetServiceDto.getPetName());
        Assertions.assertEquals(PetTypeEnum.DOG, createdPetServiceDto.getPetType());
        Assertions.assertEquals(1, createdPetServiceDto.getPetAge());
    }

    @Test
    public void testUpdatePet() {
        PetDoc petDoc = new PetDoc.Builder()
                .setPetId(samplePetId)
                .setPetName("Buddy")
                .setPetType("DOG")
                .setPetAge(5)
                .build();

        Mockito.when(petDocRepository.findById(samplePetId)).thenReturn(Optional.of(petDoc));
        Mockito.when(petDocRepository.save(petDoc)).thenReturn(petDoc);

        PetServiceDto petServiceDto = ImmutablePetServiceDto.builder()
                .petId(samplePetId)
                .petName("Buddy Updated")
                .petType(PetTypeEnum.DOG)
                .petAge(6)
                .build();

        PetServiceDto updatedPet = petService.updatePet(samplePetId, petServiceDto);

        Assertions.assertNotNull(updatedPet);
        Assertions.assertEquals(samplePetId, updatedPet.getPetId());
        Assertions.assertEquals("Buddy Updated", updatedPet.getPetName());
        Assertions.assertEquals(PetTypeEnum.DOG, updatedPet.getPetType());
        Assertions.assertEquals(6, updatedPet.getPetAge());
    }

    @Test
    public void testUpdatePet_NotFound() {
        Mockito.when(petDocRepository.findById(samplePetId)).thenReturn(Optional.empty());

        PetServiceDto petServiceDto = ImmutablePetServiceDto.builder()
                .petId(samplePetId)
                .petName("Buddy Updated")
                .petType(PetTypeEnum.DOG)
                .petAge(6)
                .build();

        PetServiceDto updatedPet = petService.updatePet(samplePetId, petServiceDto);

        Assertions.assertNull(updatedPet);
    }

    @Test
    public void testDeletePetByID() {
        Mockito.when(petDocRepository.existsById(samplePetId)).thenReturn(true);

        boolean deleted = petService.deletePetByID(samplePetId);

        Assertions.assertTrue(deleted);
    }

    @Test
    public void testDeletePetByID_NotFound() {
        Mockito.when(petDocRepository.existsById(samplePetId)).thenReturn(false);

        boolean deleted = petService.deletePetByID(samplePetId);

        Assertions.assertFalse(deleted);
    }
}