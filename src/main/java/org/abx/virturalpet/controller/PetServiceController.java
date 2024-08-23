package org.abx.virturalpet.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import org.abx.virturalpet.dto.ImmutablePetServiceDto;
import org.abx.virturalpet.dto.PetServiceDto;
import org.abx.virturalpet.service.PetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
// Add other specific imports as needed

@RestController
public class PetServiceController {
    private final PetService petService;

    public PetServiceController(PetService petService) {
        this.petService = petService;
    }

    @RequestMapping(value = "/pets/{pet_id}", method = RequestMethod.GET)
    public ResponseEntity<PetServiceDto> getPetDocument(@PathVariable("pet_id") UUID petId) {
        PetServiceDto petServiceDto = petService.searchPetByID(petId);

        if (petServiceDto == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(petServiceDto);
    }

    @RequestMapping(value = "/pets/{pet_id}", method = RequestMethod.PUT)
    public ResponseEntity<PetServiceDto> updatePetDocument(
            @PathVariable("pet_id") UUID petId, @RequestBody ImmutablePetServiceDto petServiceDto) {
        PetServiceDto updatedPet = petService.updatePet(petId, petServiceDto);

        if (updatedPet == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedPet);
    }

    @RequestMapping(value = "/pets/{pet_id}", method = RequestMethod.DELETE)
    public ResponseEntity<PetServiceDto> deletePetDocument(@PathVariable("pet_id") UUID petId) {

        if (petService.deletePetByID(petId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/pets", method = RequestMethod.POST)
    public ResponseEntity<PetServiceDto> createPetDocument(@RequestBody PetServiceDto petServiceDto)
            throws URISyntaxException {
        PetServiceDto pet = petService.createPet(petServiceDto);
        if (pet == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.created(new URI("/pets/" + pet.getPetId())).body(pet);
    }
}
