package org.abx.virturalpet.service;

import java.util.Optional;
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

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSearchPetByID() {
        PetDoc petDoc = new PetDoc.Builder()
                .setPetId(1L) // cast from int to long
                .setPetName("Rover")
                .setPetType("DOG")
                .setPetAge(5)
                .build();
        Mockito.when(petDocRepository.findById(1L)).thenReturn(Optional.of(petDoc));

        PetServiceDto petServiceDto = petService.searchPetByID(1);

        Assertions.assertNotNull(petServiceDto);
        Assertions.assertEquals(1, petServiceDto.getPetId());
        Assertions.assertEquals("Rover", petServiceDto.getPetName());
        Assertions.assertEquals(PetTypeEnum.DOG, petServiceDto.getPetType());
        Assertions.assertEquals(5, petServiceDto.getPetAge());
    }

    @Test
    public void testSearchPetByIdNotFound() {
        Mockito.when(petDocRepository.findById(1L)).thenReturn(Optional.empty());

        PetServiceDto petServiceDto = petService.searchPetByID(1);

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
                .setPetId(1L)
                .setPetName("Rover")
                .setPetType("DOG")
                .setPetAge(1)
                .build();

        // mockito any matcher for any object
        Mockito.when(petDocRepository.save(Mockito.any(PetDoc.class))).thenReturn(petDoc);

        PetServiceDto createdPetServiceDto = petService.createPet(petServiceDto);

        Assertions.assertNotNull(createdPetServiceDto);
        Assertions.assertEquals(1, createdPetServiceDto.getPetId());
        Assertions.assertEquals("Rover", createdPetServiceDto.getPetName());
        Assertions.assertEquals(PetTypeEnum.DOG, createdPetServiceDto.getPetType());
        Assertions.assertEquals(1, createdPetServiceDto.getPetAge());
    }

    @Test
    public void testUpdatePet() {
        PetDoc petDoc = new PetDoc.Builder()
                .setPetId(1L)
                .setPetName("Buddy")
                .setPetType("DOG")
                .setPetAge(5)
                .build();

        Mockito.when(petDocRepository.findById(1L)).thenReturn(Optional.of(petDoc));
        Mockito.when(petDocRepository.save(petDoc)).thenReturn(petDoc);

        PetServiceDto petServiceDto = ImmutablePetServiceDto.builder()
                .petId(1)
                .petName("Buddy Updated")
                .petType(PetTypeEnum.DOG)
                .petAge(6)
                .build();

        PetServiceDto updatedPet = petService.updatePet(1, petServiceDto);

        Assertions.assertNotNull(updatedPet);
        Assertions.assertEquals(1, updatedPet.getPetId());
        Assertions.assertEquals("Buddy Updated", updatedPet.getPetName());
        Assertions.assertEquals(PetTypeEnum.DOG, updatedPet.getPetType());
        Assertions.assertEquals(6, updatedPet.getPetAge());
    }

    @Test
    public void testUpdatePet_NotFound() {
        Mockito.when(petDocRepository.findById(1L)).thenReturn(Optional.empty());

        PetServiceDto petServiceDto = ImmutablePetServiceDto.builder()
                .petId(1)
                .petName("Buddy Updated")
                .petType(PetTypeEnum.DOG)
                .petAge(6)
                .build();

        PetServiceDto updatedPet = petService.updatePet(1, petServiceDto);

        Assertions.assertNull(updatedPet);
    }

    @Test
    public void testDeletePetByID() {
        Mockito.when(petDocRepository.existsById(1L)).thenReturn(true);

        boolean deleted = petService.deletePetByID(1);

        Assertions.assertTrue(deleted);
    }

    @Test
    public void testDeletePetByID_NotFound() {
        Mockito.when(petDocRepository.existsById(1L)).thenReturn(false);

        boolean deleted = petService.deletePetByID(1);

        Assertions.assertFalse(deleted);
    }
}
