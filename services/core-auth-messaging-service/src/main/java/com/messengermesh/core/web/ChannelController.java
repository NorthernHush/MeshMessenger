package com.messengermesh.core.web;

import com.messengermesh.core.model.Channel;
import com.messengermesh.core.model.Profile;
import com.messengermesh.core.repo.ChannelRepository;
import com.messengermesh.core.repo.ProfileRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/channels")
public class ChannelController {
    private final ChannelRepository channels;
    private final ProfileRepository profiles;

    public ChannelController(ChannelRepository channels, ProfileRepository profiles){ this.channels = channels; this.profiles = profiles; }

    @PostMapping
    @io.swagger.v3.oas.annotations.Operation(summary = "Create a channel")
    public ResponseEntity<?> create(@RequestHeader("Authorization") String auth, @RequestBody Map<String,String> body){
        String userId = extractUser(auth); if (userId==null) return ResponseEntity.status(401).build();
        Channel c = new Channel(); c.setName(body.getOrDefault("name","")); c.setType(body.getOrDefault("type","PRIVATE")); c.setOwnerId(userId); c.getMembers().add(userId); c.setMembers(c.getMembers());
        channels.save(c);
        return ResponseEntity.status(201).body(c);
    }

    @GetMapping("/{id}")
    @io.swagger.v3.oas.annotations.Operation(summary = "Get channel by id")
    public ResponseEntity<?> get(@RequestHeader("Authorization") String auth, @PathVariable String id){
        String userId = extractUser(auth); if (userId==null) return ResponseEntity.status(401).build();
        Optional<Channel> c = channels.findById(id); if (c.isEmpty()) return ResponseEntity.status(404).build();
        if (!c.get().getMembers().contains(userId)) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(c.get());
    }

    @PostMapping("/{id}/members")
    @io.swagger.v3.oas.annotations.Operation(summary = "Add member to channel (owner only)")
    public ResponseEntity<?> addMember(@RequestHeader("Authorization") String auth, @PathVariable String id, @RequestBody Map<String,String> body){
        String userId = extractUser(auth); if (userId==null) return ResponseEntity.status(401).build();
        Optional<Channel> c = channels.findById(id); if (c.isEmpty()) return ResponseEntity.status(404).build();
        Channel ch = c.get(); if (!ch.getOwnerId().equals(userId)) return ResponseEntity.status(403).build();
        String memberId = body.get("memberId"); ch.getMembers().add(memberId); channels.save(ch); return ResponseEntity.ok(ch);
    }

    @DeleteMapping("/{id}/members/{memberId}")
    @io.swagger.v3.oas.annotations.Operation(summary = "Remove member from channel (owner only)")
    public ResponseEntity<?> removeMember(@RequestHeader("Authorization") String auth, @PathVariable String id, @PathVariable String memberId){
        String userId = extractUser(auth); if (userId==null) return ResponseEntity.status(401).build();
        Optional<Channel> c = channels.findById(id); if (c.isEmpty()) return ResponseEntity.status(404).build();
        Channel ch = c.get(); if (!ch.getOwnerId().equals(userId)) return ResponseEntity.status(403).build();
        ch.getMembers().remove(memberId); channels.save(ch); return ResponseEntity.ok(ch);
    }

    private String extractUser(String auth){ if (auth==null||!auth.startsWith("Bearer ")) return null; try{ return new com.messengermesh.core.security.JwtUtil(System.getProperty("APP_JWT_SECRET","changemechangemechangeme")).parseSubject(auth.substring(7)); }catch(Exception e){return null;} }
}
