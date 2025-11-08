package com.messengermesh.core.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "messages")
@Schema(description = "Message stored as opaque content; clients handle encryption/decryption")
public class Message {
    @Id
    private String id = UUID.randomUUID().toString();
    private String channelId;
    private String authorId;
    private String payload; // base64 encrypted_message_blob
    private String nonce; // base64 nonce
    private String senderKeyFingerprint;
    private java.util.List<String> recipientKeyFingerprints;
    private Instant timestamp = Instant.now();
    private boolean encrypted = true;

    public String getId() { return id; }
    public String getChannelId() { return channelId; }
    public void setChannelId(String channelId) { this.channelId = channelId; }
    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public String getNonce() { return nonce; }
    public void setNonce(String nonce) { this.nonce = nonce; }
    public String getSenderKeyFingerprint() { return senderKeyFingerprint; }
    public void setSenderKeyFingerprint(String senderKeyFingerprint) { this.senderKeyFingerprint = senderKeyFingerprint; }
    public java.util.List<String> getRecipientKeyFingerprints() { return recipientKeyFingerprints; }
    public void setRecipientKeyFingerprints(java.util.List<String> recipientKeyFingerprints) { this.recipientKeyFingerprints = recipientKeyFingerprints; }
    public Instant getTimestamp() { return timestamp; }
    public boolean isEncrypted() { return encrypted; }
    public void setEncrypted(boolean encrypted) { this.encrypted = encrypted; }
}
