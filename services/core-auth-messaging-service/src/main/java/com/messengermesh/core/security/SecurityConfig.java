package com.messengermesh.core.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import com.messengermesh.core.security.JwtUtil;


@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    JwtUtil jwtUtil = new JwtUtil(System.getProperty("APP_JWT_SECRET", "changemechangemechangeme"));
    http.csrf().disable()
        .addFilterBefore(new RateLimitFilter(authBucket()), JwtAuthenticationFilter.class)
        .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests((auth) -> auth
            .requestMatchers("/api/v1/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/ws/**").permitAll()
            .anyRequest().authenticated()
        )
        .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public CorsFilter corsFilter(){
        var config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(java.util.List.of("http://localhost:3000"));
        config.setAllowedMethods(java.util.List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(java.util.List.of("Authorization","Content-Type"));
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    // simple rate limiter bucket for auth endpoints
    @Bean
    public Bucket authBucket(){
        Bandwidth limit = Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void validateSecret(){
        String s = System.getProperty("APP_JWT_SECRET", System.getenv("APP_JWT_SECRET"));
        if (s==null || s.length()<32) throw new IllegalStateException("APP_JWT_SECRET must be set and at least 32 bytes long");
    }
}
