package org.abx.virturalpet.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.abx.virturalpet.model.PetDoc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetDocRepository extends JpaRepository<PetDoc, Long> {
    // make it optional
    Optional<List<PetDoc>> findByPetName(String name);

    Optional<List<PetDoc>> findByPetType(String type);

    Optional<List<PetDoc>> findByPetBreed(String breed);

    Optional<List<PetDoc>> findByPetAge(int age);

    Optional<List<PetDoc>> findByPetBirthdate(java.sql.Date birthdate);

    Optional<List<PetDoc>> findByPetGender(String gender);

    Optional<List<PetDoc>> findByPetColor(String color);

    Optional<List<PetDoc>> findByOwnerId(UUID ownerId);
}
