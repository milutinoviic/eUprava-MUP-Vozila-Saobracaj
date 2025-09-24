package com.example.auth.repo;

import com.example.auth.model.AuthUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<AuthUser, String> {
    Optional<AuthUser> findByEmail(String email);
    boolean existsByEmail(String email);
}
