package com.messengermesh.core.web;

import com.messengermesh.core.model.Profile;
import com.messengermesh.core.model.RefreshTokenDoc;
import com.messengermesh.core.model.UserDocument;
import com.messengermesh.core.repo.ProfileRepository;
import com.messengermesh.core.repo.RefreshTokenDocRepository;
import com.messengermesh.core.repo.UserRepository;
import com.messengermesh.core.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    record RegisterReq(@Email String email, @NotBlank String password, String displayName){}
    record LoginReq(@Email String email, @NotBlank String password){}

    private final UserRepository users;
    private final ProfileRepository profiles;
    private final RefreshTokenDocRepository refreshRepo;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    public AuthController(UserRepository users, ProfileRepository profiles, RefreshTokenDocRepository refreshRepo,
                          @Value("${APP_JWT_SECRET:changemechangemechangeme}") String secret){
        this.users = users; this.profiles = profiles; this.refreshRepo = refreshRepo; this.jwtUtil = new JwtUtil(secret);
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user", responses = { @ApiResponse(responseCode = "201", description = "Created") })
    public ResponseEntity<?> register(@Valid @RequestBody RegisterReq req){
        if (users.findByEmail(req.email()).isPresent()) return ResponseEntity.status(409).body(Map.of("error","email_taken"));
        UserDocument u = new UserDocument();
        u.setEmail(req.email());
        u.setPasswordHash(passwordEncoder.encode(req.password()));
        users.save(u);
        Profile p = new Profile(); p.setUserId(u.getId()); p.setDisplayName(req.displayName()!=null?req.displayName():req.email());
        profiles.save(p);
        return ResponseEntity.status(201).body(Map.of("id", u.getId()));
    }

    @PostMapping("/login")
    @Operation(summary = "Login user and return tokens", responses = { @ApiResponse(responseCode = "200", description = "Tokens") })
    public ResponseEntity<?> login(@Valid @RequestBody LoginReq req){
        Optional<UserDocument> opt = users.findByEmail(req.email());
        if (opt.isEmpty()) return ResponseEntity.status(401).build();
        UserDocument u = opt.get();
        if (!passwordEncoder.matches(req.password(), u.getPasswordHash())) return ResponseEntity.status(401).build();
        String access = jwtUtil.generateToken(u.getId(), 60*15);
    String refresh = UUID.randomUUID().toString();
    String hashed = org.apache.commons.codec.digest.DigestUtils.sha256Hex(refresh);
    RefreshTokenDoc tok = new RefreshTokenDoc(); tok.setUserId(u.getId()); tok.setToken(hashed); tok.setExpiresAt(Instant.now().plusSeconds(60L*60*24*30));
    refreshRepo.save(tok);
    return ResponseEntity.ok(Map.of("accessToken", access, "refreshToken", refresh));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token given a refresh token")
    public ResponseEntity<?> refresh(@RequestBody Map<String,String> body){
        String rt = body.get("refreshToken");
        if (rt == null) return ResponseEntity.badRequest().build();
    String hashed = org.apache.commons.codec.digest.DigestUtils.sha256Hex(rt);
    Optional<RefreshTokenDoc> r = refreshRepo.findByToken(hashed);
    if (r.isEmpty()) return ResponseEntity.status(401).build();
    if (r.get().getExpiresAt().isBefore(Instant.now())) { refreshRepo.delete(r.get()); return ResponseEntity.status(401).build(); }
    // rotate
    refreshRepo.delete(r.get());
    String newRefresh = UUID.randomUUID().toString();
    String newHashed = org.apache.commons.codec.digest.DigestUtils.sha256Hex(newRefresh);
    RefreshTokenDoc newTok = new RefreshTokenDoc(); newTok.setUserId(r.get().getUserId()); newTok.setToken(newHashed); newTok.setExpiresAt(Instant.now().plusSeconds(60L*60*24*30));
    refreshRepo.save(newTok);
    String accessNew = jwtUtil.generateToken(r.get().getUserId(), 60*15);
    return ResponseEntity.ok(Map.of("accessToken", accessNew, "refreshToken", newRefresh));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user's profile")
    public ResponseEntity<?> me(@RequestHeader("Authorization") String auth){
        if (auth==null||!auth.startsWith("Bearer ")) return ResponseEntity.status(401).build();
        try{
            String sub = jwtUtil.parseSubject(auth.substring(7));
            Optional<Profile> p = profiles.findByUserId(sub);
            if (p.isEmpty()) return ResponseEntity.status(404).build();
            return ResponseEntity.ok(p.get());
        }catch(Exception e){
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String,String> body){
        String rt = body.get("refreshToken");
        if (rt==null) return ResponseEntity.badRequest().build();
        String hashed = org.apache.commons.codec.digest.DigestUtils.sha256Hex(rt);
        refreshRepo.findByToken(hashed).ifPresent(refreshRepo::delete);
        return ResponseEntity.ok(Map.of("status","ok"));
    }
}
package com.messengermesh.core.web;

import com.messengermesh.core.model.User;
import com.messengermesh.core.repo.UserRepository;
import com.messengermesh.core.security.JwtUtil;
import com.messengermesh.core.web.dto.AuthDtos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository, @Value("${APP_JWT_SECRET:changemechangemechangeme}") String secret) {
        this.userRepository = userRepository;
        this.jwtUtil = new JwtUtil(secret);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthDtos.RegisterRequest req) {
        if (req.username == null || req.password == null) return ResponseEntity.badRequest().build();
        Optional<User> exists = userRepository.findByUsername(req.username);
        if (exists.isPresent()) return ResponseEntity.status(409).body("username_taken");
        User u = new User();
        u.setUsername(req.username);
        u.setPasswordHash(passwordEncoder.encode(req.password));
        userRepository.save(u);
        return ResponseEntity.status(201).body(u);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthDtos.LoginRequest req) {
        if (req.username == null || req.password == null) return ResponseEntity.badRequest().build();
        Optional<User> opt = userRepository.findByUsername(req.username);
        if (opt.isEmpty()) return ResponseEntity.status(401).build();
        User u = opt.get();
        if (!passwordEncoder.matches(req.password, u.getPasswordHash())) return ResponseEntity.status(401).build();
        AuthDtos.AuthResponse resp = new AuthDtos.AuthResponse();
        resp.accessToken = jwtUtil.generateToken(u.getId().toString(), 60 * 15); // 15m
        resp.refreshToken = jwtUtil.generateToken(u.getId().toString(), 60 * 60 * 24 * 30); // 30d
        return ResponseEntity.ok(resp);
    }
}
