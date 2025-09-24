package com.example.auth.service;

import com.example.auth.model.*;

import java.util.Map;

public interface UserService {
    AuthResponse login(LoginRequest request);
    AuthUser register(AuthUser user);
    Map<String, Object> verify(String token);
}
