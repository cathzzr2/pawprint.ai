package org.abx.virturalpet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "pets", schema = "virtual_pet_schema")
public class PetDoc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pet_id", updatable = false, nullable = false)
    private UUID petId; // schema uses SERIAL type here

    @Column(name = "pet_name", length = 50, nullable = false)
    private String petName;

    @Column(name = "pet_type", length = 50, nullable = false)
    private String petType;

    @Column(name = "pet_breed", length = 50, nullable = false)
    private String petBreed;

    @Column(name = "pet_age", length = 50, nullable = false)
    private int petAge; // schema used SMALLINT type here

    @Column(name = "pet_birthdate", nullable = false)
    private java.sql.Date petBirthdate;

    @Column(name = "pet_gender", length = 10, nullable = false)
    private String petGender;

    @Column(name = "pet_color", length = 50, nullable = false)
    private String petColor;

    @Column(name = "owner_id", unique = true, nullable = false)
    private UUID ownerId;

    public PetDoc() {}

    public PetDoc(
            UUID petId,
            String petName,
            String petType,
            String petBreed,
            int petAge,
            java.sql.Date petBirthdate,
            String petGender,
            String petColor,
            UUID ownerId) {
        this.petId = petId;
        this.petName = petName;
        this.petType = petType;
        this.petBreed = petBreed;
        this.petAge = petAge;
        this.petBirthdate = petBirthdate;
        this.petGender = petGender;
        this.petColor = petColor;
        this.ownerId = ownerId;
    }

    public UUID getPetId() {
        return petId;
    }

    public void setPetId(UUID petId) {
        this.petId = petId;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetType() {
        return petType;
    }

    public void setPetType(String petType) {
        this.petType = petType;
    }

    public String getPetBreed() {
        return petBreed;
    }

    public void setPetBreed(String petBreed) {
        this.petBreed = petBreed;
    }

    public int getPetAge() {
        return petAge;
    }

    public void setPetAge(int petAge) {
        this.petAge = petAge;
    }

    public java.sql.Date getPetBirthdate() {
        return petBirthdate;
    }

    public void setPetBirthdate(java.sql.Date petBirthdate) {
        this.petBirthdate = petBirthdate;
    }

    public String getPetGender() {
        return petGender;
    }

    public void setPetGender(String petGender) {
        this.petGender = petGender;
    }

    public String getPetColor() {
        return petColor;
    }

    public void setPetColor(String petColor) {
        this.petColor = petColor;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public String toString() {
        return "PetDoc{" + "petId="
                + petId + ", petName='"
                + petName + '\'' + ", petType='"
                + petType + '\'' + ", petBreed='"
                + petBreed + '\'' + ", petAge="
                + petAge + ", petBirthdate="
                + petBirthdate + ", petGender='"
                + petGender + '\'' + ", petColor='"
                + petColor + '\'' + ", ownerId="
                + ownerId + '}';
    }

    public static class Builder {
        private UUID petId;
        private String petName;
        private String petType;
        private String petBreed;
        private int petAge;
        private java.sql.Date petBirthdate;
        private String petGender;
        private String petColor;
        private UUID ownerId;

        public Builder setPetId(UUID petId) {
            this.petId = petId;
            return this;
        }

        public Builder setPetName(String petName) {
            this.petName = petName;
            return this;
        }

        public Builder setPetType(String petType) {
            this.petType = petType;
            return this;
        }

        public Builder setPetBreed(String petBreed) {
            this.petBreed = petBreed;
            return this;
        }

        public Builder setPetAge(int petAge) {
            this.petAge = petAge;
            return this;
        }

        public Builder setPetBirthdate(java.sql.Date petBirthdate) {
            this.petBirthdate = petBirthdate;
            return this;
        }

        public Builder setPetGender(String petGender) {
            this.petGender = petGender;
            return this;
        }

        public Builder setPetColor(String petColor) {
            this.petColor = petColor;
            return this;
        }

        public Builder setOwnerId(UUID ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        public PetDoc build() {
            return new PetDoc(petId, petName, petType, petBreed, petAge, petBirthdate, petGender, petColor, ownerId);
        }
    }
}
