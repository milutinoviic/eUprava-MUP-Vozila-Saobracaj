package com.example.auth.controller;

import com.example.auth.model.AuthUser;
import com.example.auth.model.LoginRequest;
import com.example.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthUser req) {
        return ResponseEntity.status(201).body(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestHeader("Authorization") String bearer) {
        if (bearer == null || !bearer.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "missing bearer token"));
        }
        String token = bearer.substring(7);
        return ResponseEntity.ok(authService.verify(token));
    }
}
