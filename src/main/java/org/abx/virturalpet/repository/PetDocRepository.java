package org.abx.virturalpet.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.abx.virturalpet.model.PetDoc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetDocRepository extends JpaRepository<PetDoc, Long> {
    // make it optional
    Optional<List<PetDoc>> findByName(String name);

    Optional<List<PetDoc>> findByType(String type);

    Optional<List<PetDoc>> findByBreed(String breed);

    Optional<List<PetDoc>> findByAge(int age);

    Optional<List<PetDoc>> findByBirthdate(java.sql.Date birthdate);

    Optional<List<PetDoc>> findByGender(String gender);

    Optional<List<PetDoc>> findByColor(String color);

    Optional<List<PetDoc>> findByOwnerId(UUID ownerId);
}
