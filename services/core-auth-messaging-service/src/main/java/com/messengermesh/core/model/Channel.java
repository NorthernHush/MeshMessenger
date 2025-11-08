package com.messengermesh.core.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "channels")
@Schema(description = "Channel (conversation) metadata")
public class Channel {
    @Id
    private String id = UUID.randomUUID().toString();
    private String name;
    private String type; // PRIVATE or PUBLIC
    private String ownerId;
    private List<String> members = new ArrayList<>();
    private Instant createdAt = Instant.now();

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    public List<String> getMembers() { return members; }
    public void setMembers(List<String> members) { this.members = members; }
    public Instant getCreatedAt() { return createdAt; }
}
