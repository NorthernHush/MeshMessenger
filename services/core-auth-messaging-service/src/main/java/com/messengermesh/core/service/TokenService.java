package com.messengermesh.core.service;

import com.messengermesh.core.model.RefreshToken;
import com.messengermesh.core.repo.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class TokenService {
    private final RefreshTokenRepository repo;

    public TokenService(RefreshTokenRepository repo){ this.repo = repo; }

    public RefreshToken createRefreshToken(UUID userId, Instant expiresAt, String tokenHash){
        RefreshToken t = new RefreshToken();
        t.setUserId(userId);
        t.setTokenId(UUID.randomUUID().toString());
        t.setTokenHash(tokenHash);
        t.setExpiresAt(expiresAt);
        return repo.save(t);
    }

    public void revoke(RefreshToken token){
        repo.delete(token);
    }

    public java.util.Optional<RefreshToken> findByTokenId(String tokenId){
        return repo.findByTokenId(tokenId);
    }
}
