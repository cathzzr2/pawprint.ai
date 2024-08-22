package org.abx.virturalpet.repository;

import java.util.Optional;
import java.util.UUID;
import org.abx.virturalpet.model.UserDoc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDocRepository extends JpaRepository<UserDoc, UUID> {
    Optional<UserDoc> findByUserName(String userName);

    Optional<UserDoc> findByEmail(String email);

    Optional<UserDoc> findByPetIds(Integer petIds);

    Optional<UserDoc> findByLastActive(java.sql.Timestamp lastActive);
}