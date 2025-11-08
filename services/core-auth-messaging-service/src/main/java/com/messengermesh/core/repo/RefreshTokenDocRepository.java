package com.messengermesh.core.repo;

import com.messengermesh.core.model.RefreshTokenDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RefreshTokenDocRepository extends MongoRepository<RefreshTokenDoc, String> {
    Optional<RefreshTokenDoc> findByToken(String token);
}
