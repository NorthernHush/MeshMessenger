package com.messengermesh.core.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.time.Instant;
import java.util.Date;

public class JwtUtil {
    private final byte[] secret;

    public JwtUtil(String secret) {
        this.secret = secret.getBytes();
    }

    public String generateToken(String subject, long secondsValid) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(secondsValid)))
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret), SignatureAlgorithm.HS256)
                .compact();
    }

    public String parseSubject(String token){
        return Jwts.parserBuilder().setSigningKey(io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret)).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
}
