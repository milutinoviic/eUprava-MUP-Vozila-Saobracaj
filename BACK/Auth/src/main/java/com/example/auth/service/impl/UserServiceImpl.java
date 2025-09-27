package com.example.auth.service.impl;

import com.example.auth.model.AuthResponse;
import com.example.auth.model.AuthUser;
import com.example.auth.model.LoginRequest;
import com.example.auth.repo.UserRepository;
import com.example.auth.security.JwtService;
import com.example.auth.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RestClient restClient;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${police.service.url}")
    private String policeServiceUrl;

    public UserServiceImpl(UserRepository userRepository,
                           JwtService jwtService,
                           RestClient.Builder restClientBuilder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.restClient = restClientBuilder.build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        AuthUser user = userRepository.findByEmail(request.getEmail().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword().trim(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        String token = jwtService.generateToken(
                user.getEmail(),

                user.getRole().name(),
                user.getId(),
                10 * 60 * 1000 // 10 minutes
        );
        return new AuthResponse(token, user);
    }

    @Override
    public AuthUser register(AuthUser req) {
        // Check if email already exists
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        // Create user entity
        AuthUser user = AuthUser.builder()
                .email(req.getEmail().trim())
                .firstName(req.getFirstName().trim())
                .lastName(req.getLastName().trim())
                .password(passwordEncoder.encode(req.getPassword().trim()))
                .role(req.getRole())
                .build();

        AuthUser created = userRepository.save(user);

        // If role is POLICE, register in Traffic Police service
        if (created.getRole() == AuthUser.UserRole.POLICE) {
            // Generate JWT token for internal call
            String token = jwtService.generateToken(
                    user.getEmail(),
                    user.getRole().name(),
                    user.getId(),
                    5 * 60 * 1000 // 5 minutes
            );

            // Build WebClient
            WebClient webClient = WebClient.builder()
                    .baseUrl(policeServiceUrl)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();

            try {
                webClient.post()
                        .uri("/police")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .bodyValue(created)
                        .retrieve()
                        .toBodilessEntity()
                        .block(); // Execute synchronously

                log.info("✅ Successfully registered police user in Traffic Police service: {}", created.getEmail());
            } catch (Exception e) {
                log.error("❌ Error calling Traffic Police service at {}. Cause: {}", policeServiceUrl + "/police", e.getMessage(), e);
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Traffic Police service error: " + e.getMessage(),
                        e
                );
            }
        }

        // Hide password before returning
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
