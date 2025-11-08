package com.messengermesh.core;

import com.messengermesh.core.model.UserDocument;
import com.messengermesh.core.repo.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public class AuthIntegrationTest {
    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:6.0.8");

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry r){
        r.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @Autowired
    private UserRepository users;

    @Test
    public void testCreateUser(){
        UserDocument u = new UserDocument(); u.setEmail("test@example.com"); u.setPasswordHash("hash");
        users.save(u);
        Assertions.assertTrue(users.findByEmail("test@example.com").isPresent());
    }
}
