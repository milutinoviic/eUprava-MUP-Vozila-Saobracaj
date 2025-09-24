package com.example.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")          // all endpoints
                        .allowedOrigins("*")        // allow any origin
                        .allowedMethods("*")        // allow any HTTP method
                        .allowedHeaders("*")        // allow any headers
                        .allowCredentials(false);   // cannot be true with "*" origin
            }
        };
    }
}

