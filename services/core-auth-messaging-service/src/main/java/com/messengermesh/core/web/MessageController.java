package com.messengermesh.core.web;

import com.messengermesh.core.model.Channel;
import com.messengermesh.core.model.Message;
import com.messengermesh.core.repo.ChannelRepository;
import com.messengermesh.core.repo.MessageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/channels/{channelId}/messages")
public class MessageController {
    private final MessageRepository messages;
    private final ChannelRepository channels;

    public MessageController(MessageRepository messages, ChannelRepository channels){ this.messages = messages; this.channels = channels; }

    @PostMapping
    @io.swagger.v3.oas.annotations.Operation(summary = "Send a message to channel (server stores opaque blob)")
    public ResponseEntity<?> send(@RequestHeader("Authorization") String auth, @PathVariable String channelId, @RequestBody Map<String,Object> body){
        String userId = extractUser(auth); if (userId==null) return ResponseEntity.status(401).build();
        Optional<Channel> c = channels.findById(channelId); if (c.isEmpty()) return ResponseEntity.status(404).build();
        if (!c.get().getMembers().contains(userId)) return ResponseEntity.status(403).build();
        Message m = new Message(); m.setChannelId(channelId); m.setAuthorId(userId); m.setContent((String)body.get("content")); m.setEncrypted((Boolean)body.getOrDefault("encrypted", true));
        messages.save(m);
        return ResponseEntity.status(201).body(m);
    }

    @GetMapping
    @io.swagger.v3.oas.annotations.Operation(summary = "Get message history for channel (paginated)")
    public ResponseEntity<?> history(@RequestHeader("Authorization") String auth, @PathVariable String channelId, @RequestParam(defaultValue = "50") int limit, @RequestParam(defaultValue = "0") int page){
        String userId = extractUser(auth); if (userId==null) return ResponseEntity.status(401).build();
        Optional<Channel> c = channels.findById(channelId); if (c.isEmpty()) return ResponseEntity.status(404).build();
        Message m = new Message(); m.setChannelId(channelId); m.setAuthorId(userId);
        // expected payload structure
        m.setPayload((String)body.get("payload"));
        m.setNonce((String)body.get("nonce"));
        m.setSenderKeyFingerprint((String)body.get("senderKeyFingerprint"));
        m.setRecipientKeyFingerprints((java.util.List<String>)body.getOrDefault("recipientKeyFingerprints", java.util.List.of()));
        m.setEncrypted((Boolean)body.getOrDefault("encrypted", true));
        // optional signature verification (server may verify signature using stored publicSigningKey)
        String signature = (String) body.get("signature");
        if (signature != null){
            // attempt to verify signature, but do NOT store plaintext
            try{
                var profile = profiles.findByUserId(userId);
                if (profile.isPresent() && profile.get().getPublicSigningKey()!=null){
                    // verify Ed25519 signature using publicSigningKey (base64)
                    byte[] pub = java.util.Base64.getDecoder().decode(profile.get().getPublicSigningKey());
                    byte[] sig = java.util.Base64.getDecoder().decode(signature);
                    byte[] payload = java.util.Base64.getDecoder().decode(m.getPayload());
                    java.security.PublicKey pk = java.security.KeyFactory.getInstance("Ed25519").generatePublic(new java.security.spec.X509EncodedKeySpec(pub));
                    java.security.Signature sigInst = java.security.Signature.getInstance("Ed25519");
                    sigInst.initVerify(pk);
                    sigInst.update(payload);
                    boolean ok = sigInst.verify(sig);
                    if (!ok) return ResponseEntity.status(400).body(Map.of("error","invalid_signature"));
                }
            }catch(Exception ex){
                return ResponseEntity.status(400).body(Map.of("error","signature_verification_failed","details",ex.getMessage()));
            }
        }
        var pg = messages.findByChannelIdOrderByTimestampDesc(channelId, PageRequest.of(page, Math.min(limit,100)));
        return ResponseEntity.ok(pg.map(m->m));
    }

    private String extractUser(String auth){ if (auth==null||!auth.startsWith("Bearer ")) return null; try{ return new com.messengermesh.core.security.JwtUtil(System.getProperty("APP_JWT_SECRET","changemechangemechangeme")).parseSubject(auth.substring(7)); }catch(Exception e){return null;} }
}
