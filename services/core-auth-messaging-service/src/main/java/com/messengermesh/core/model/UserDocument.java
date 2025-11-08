package com.messengermesh.core.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "users")
@Schema(description = "User credential document")
public class UserDocument {
    @Id
    private String id = UUID.randomUUID().toString();

    @Indexed(unique = true)
    private String email;

    private String passwordHash;
    private Instant createdAt = Instant.now();

    public String getId() { return id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Instant getCreatedAt() { return createdAt; }
}
