package org.abx.virturalpet.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.abx.virturalpet.model.PetDoc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetDocRepository extends JpaRepository<PetDoc, UUID> {

    Optional<List<PetDoc>> findByPetName(String petName);

    Optional<List<PetDoc>> findByPetType(String petType);

    Optional<List<PetDoc>> findByPetBreed(String petBreed);

    Optional<List<PetDoc>> findByPetAge(int petAge);

    Optional<List<PetDoc>> findByPetBirthdate(java.sql.Date petBirthdate);

    Optional<List<PetDoc>> findByPetGender(String petGender);

    Optional<List<PetDoc>> findByPetColor(String petColor);

    Optional<List<PetDoc>> findByOwnerId(UUID ownerId);
}