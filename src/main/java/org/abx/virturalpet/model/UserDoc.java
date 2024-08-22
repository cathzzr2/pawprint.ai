package org.abx.virturalpet.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users", schema = "virtual_pet_schema")
public class UserDoc {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;

    @Column(name = "user_name", length = 50, unique = true, nullable = false)
    private String userName;

    @Column(name = "password", length = 50, nullable = false)
    private String password;

    @Column(name = "email", length = 50, unique = true, nullable = false)
    private String email;

    @ElementCollection
    @CollectionTable(name = "user_pet_ids", schema = "virtual_pet_schema", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "petIds")
    private List<Integer> petIds;

    @Column(name = "last_active", nullable = false)
    private java.sql.Timestamp lastActive;

    public UserDoc() {}

    public UserDoc(
            UUID userId,
            String userName,
            String password,
            String email,
            List<Integer> petIds,
            java.sql.Timestamp lastActive) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.petIds = petIds;
        this.lastActive = lastActive;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Integer> getPetIds() {
        return petIds;
    }

    public void setPetIds(List<Integer> petIds) {
        this.petIds = petIds;
    }

    public java.sql.Timestamp getLastActive() {
        return lastActive;
    }

    public void setLastActive(java.sql.Timestamp lastActive) {
        this.lastActive = lastActive;
    }

    @Override
    public String toString() {
        return "UserDoc{"
                + "userId=" + userId
                + ", userName='" + userName + '\''
                + ", password='" + password + '\''
                + ", email='" + email + '\''
                + ", petIds=" + petIds
                + ", lastActive=" + lastActive
                + '}';
    }

    public static class Builder {
        private UUID userId;
        private String userName;
        private String password;
        private String email;
        private List<Integer> petIds;
        private java.sql.Timestamp lastActive;

        public Builder setUserId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setPetIds(List<Integer> petIds) {
            this.petIds = petIds;
            return this;
        }

        public Builder setLastActive(java.sql.Timestamp lastActive) {
            this.lastActive = lastActive;
            return this;
        }

        public UserDoc build() {
            return new UserDoc(userId, userName, password, email, petIds, lastActive);
        }
    }
}