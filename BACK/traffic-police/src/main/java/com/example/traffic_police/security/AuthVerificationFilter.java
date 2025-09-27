package com.example.traffic_police.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthVerificationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AuthVerificationFilter.class);

    @Value("${auth.service.url}")
    private String authServiceUrl;

    private final WebClient webClient;
    private final Map<String, Instant> tokenCache = new ConcurrentHashMap<>();
    private final long cacheTtlSeconds = 300; // cache tokens for 5 minutes

    public AuthVerificationFilter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        log.info("Incoming request URI: {}", path);

        // Dump headers
        Collections.list(request.getHeaderNames())
                .forEach(h -> log.info("Header {} = {}", h, request.getHeader(h)));

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            setCorsHeaders(response);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        setCorsHeaders(response);

        // Bypass /api/police (for registration)
        if (path.equals("/api/police")) {
            log.info("Bypassing auth filter for path: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7);
        log.info("Token received: {}", token);

        Instant cachedExpiry = tokenCache.get(token);
        if (cachedExpiry != null && Instant.now().isBefore(cachedExpiry)) {
            log.info("Token is cached and valid, proceeding...");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            log.info("Verifying token with auth service...");

            String authResponse = webClient.get()
                    .uri(authServiceUrl + "/verify")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // blocking call for debugging

            log.info("Auth service response: {}", authResponse);

            // Token valid
            tokenCache.put(token, Instant.now().plusSeconds(cacheTtlSeconds));
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("❌ Token verification failed: {}", e.getMessage(), e);
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
