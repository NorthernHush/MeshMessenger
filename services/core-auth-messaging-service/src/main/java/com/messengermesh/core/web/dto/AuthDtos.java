package com.messengermesh.core.web.dto;

public class AuthDtos {
    public static class RegisterRequest {
        public String username;
        public String password;
    }
    public static class LoginRequest {
        public String username;
        public String password;
    }
    public static class AuthResponse {
        public String accessToken;
        public String refreshToken;
    }
}
