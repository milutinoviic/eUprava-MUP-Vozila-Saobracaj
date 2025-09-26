package com.example.traffic_police.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthVerificationFilter extends OncePerRequestFilter {

    @Value("${auth.service.url}")
    private String authServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();


    private final Map<String, Instant> tokenCache = new ConcurrentHashMap<>();
    private final long cacheTtlSeconds = 300; // cache tokens for 5 minutes

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {



        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            setCorsHeaders(response);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }


        setCorsHeaders(response);


        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7);


        Instant cachedExpiry = tokenCache.get(token);
        if (cachedExpiry != null && Instant.now().isBefore(cachedExpiry)) {
            filterChain.doFilter(request, response);
            return;
        }


        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> authResponse = restTemplate.exchange(
                    authServiceUrl + "/verify",
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (!authResponse.getStatusCode().is2xxSuccessful()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }


            tokenCache.put(token, Instant.now().plusSeconds(cacheTtlSeconds));


            filterChain.doFilter(request, response);

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token verification failed");
        }
    }

    private void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,PATCH,DELETE,OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization,Content-Type");
        response.setHeader("Access-Control-Expose-Headers", "Authorization");
    }
}
