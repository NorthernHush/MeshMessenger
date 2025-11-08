package com.messengermesh.core.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth){
        if (auth == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(Map.of("id", auth.getPrincipal()));
    }

    record KeysDto(String publicSigningKey, String publicEncryptionKey, String keyFingerprint){}

    @PostMapping("/keys")
    public ResponseEntity<?> uploadKeys(Authentication auth, @RequestBody KeysDto dto){
        if (auth == null) return ResponseEntity.status(401).build();
        String userId = (String) auth.getPrincipal();
        var pOpt = profiles.findByUserId(userId);
        if (pOpt.isEmpty()) return ResponseEntity.status(404).build();
        var p = pOpt.get();
        p.setPublicSigningKey(dto.publicSigningKey());
        p.setPublicEncryptionKey(dto.publicEncryptionKey());
        p.setKeyFingerprint(dto.keyFingerprint());
        profiles.save(p);
        return ResponseEntity.ok(Map.of("status","ok"));
    }
}
