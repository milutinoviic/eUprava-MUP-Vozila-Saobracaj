package com.example.auth.model;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private AuthUser user;

    public AuthResponse(String token, AuthUser user) {
        this.token = token;
        this.user = user;
    }
}
