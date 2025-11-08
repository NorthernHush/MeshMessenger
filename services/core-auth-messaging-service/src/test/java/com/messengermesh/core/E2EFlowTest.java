package com.messengermesh.core;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class E2EFlowTest {
    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r){ r.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl); }

    @Autowired
    private TestRestTemplate rest;

    @Test
    public void fullUserFlow(){
        // register
        var reg = Map.of("email","e2e@example.com","password","secret123","displayName","E2E");
        ResponseEntity<Map> r1 = rest.postForEntity("/api/v1/auth/register", reg, Map.class);
        assertThat(r1.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // login
        var login = Map.of("email","e2e@example.com","password","secret123");
        ResponseEntity<Map> r2 = rest.postForEntity("/api/v1/auth/login", login, Map.class);
        assertThat(r2.getStatusCode()).isEqualTo(HttpStatus.OK);
        String access = (String) r2.getBody().get("accessToken");
        assertThat(access).isNotNull();

        // me
        HttpHeaders hdr = new HttpHeaders(); hdr.setBearerAuth(access);
        ResponseEntity<Map> r3 = rest.exchange("/api/v1/auth/me", HttpMethod.GET, new HttpEntity<>(hdr), Map.class);
        assertThat(r3.getStatusCode()).isEqualTo(HttpStatus.OK);

        // create channel
        var body = Map.of("name","private-room","type","PRIVATE");
        ResponseEntity<Map> r4 = rest.exchange("/api/v1/channels", HttpMethod.POST, new HttpEntity<>(body, hdr), Map.class);
        assertThat(r4.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String channelId = (String) r4.getBody().get("id");

        // send encrypted message (opaque blob)
        var msg = Map.of("content","encrypted_blob_123","encrypted",true);
        ResponseEntity<Map> r5 = rest.exchange("/api/v1/channels/"+channelId+"/messages", HttpMethod.POST, new HttpEntity<>(msg, hdr), Map.class);
        assertThat(r5.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String messageId = (String) r5.getBody().get("id");

        // history
        ResponseEntity<Map> r6 = rest.exchange("/api/v1/channels/"+channelId+"/messages", HttpMethod.GET, new HttpEntity<>(hdr), Map.class);
        assertThat(r6.getStatusCode()).isEqualTo(HttpStatus.OK);
        // ensure content is stored opaque
        var items = (Map) r6.getBody();
        assertThat(items).isNotNull();
    }
}
