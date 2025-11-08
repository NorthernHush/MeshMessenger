package com.messengermesh.core.security;

import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class RateLimitFilter extends OncePerRequestFilter {
    private final Bucket bucket;

    public RateLimitFilter(Bucket bucket){ this.bucket = bucket; }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/api/v1/auth/")){
            if (!bucket.tryConsume(1)){
                response.setStatus(429); return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
