package com.messengermesh.core.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "profiles")
@Schema(description = "User profile")
public class Profile {
    @Id
    private String id = UUID.randomUUID().toString();

    private String userId;
    private String displayName;
    private String avatarUrl;
    // E2EE public keys
    private String publicSigningKey; // base64 Ed25519
    private String publicEncryptionKey; // base64 X25519
    private String keyFingerprint;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getPublicSigningKey() { return publicSigningKey; }
    public void setPublicSigningKey(String publicSigningKey) { this.publicSigningKey = publicSigningKey; }
    public String getPublicEncryptionKey() { return publicEncryptionKey; }
    public void setPublicEncryptionKey(String publicEncryptionKey) { this.publicEncryptionKey = publicEncryptionKey; }
    public String getKeyFingerprint() { return keyFingerprint; }
    public void setKeyFingerprint(String keyFingerprint) { this.keyFingerprint = keyFingerprint; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
