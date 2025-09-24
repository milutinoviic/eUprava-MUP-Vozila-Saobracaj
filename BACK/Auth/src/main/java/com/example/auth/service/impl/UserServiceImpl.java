package com.example.auth.service.impl;

import com.example.auth.model.AuthResponse;
import com.example.auth.model.AuthUser;
import com.example.auth.model.LoginRequest;
import com.example.auth.repo.UserRepository;
import com.example.auth.security.JwtService;
import com.example.auth.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RestTemplate restTemplate;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${police.service.url}")
    private String policeServiceUrl;

    public UserServiceImpl(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        AuthUser user = userRepository.findByEmail(request.getEmail().trim())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword().trim(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name(),
                10 * 60 * 1000
        );
        return new AuthResponse(token, user);
    }

    @Override
    public AuthUser register(AuthUser req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        AuthUser user = AuthUser.builder()
                .email(req.getEmail().trim())
                .firstName(req.getFirstName().trim())
                .lastName(req.getLastName().trim())
                .password(passwordEncoder.encode(req.getPassword().trim()))
                .role(req.getRole())
                .build();

        AuthUser created = userRepository.save(user);
        if (created.getRole() == AuthUser.UserRole.POLICE) {
            String url = policeServiceUrl + "/police";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<AuthUser> entity = new HttpEntity<>(created, headers);
            ResponseEntity<Void> response = restTemplate.postForEntity(url, entity, Void.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to notify Traffic Police service");
            }
        }

        created.setPassword("");

        return created;
    }

    @Override
    public Map<String, Object> verify(String token) {
        var claims = jwtService.verifyToken(token);
        return Map.of(
                "ok", true,
                "email", claims.getSubject(),
                "role", claims.get("role")
        );
    }
}
