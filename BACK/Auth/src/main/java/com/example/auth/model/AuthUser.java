package com.example.auth.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AuthUser {

    @Id
    private String id;

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private UserRole role;

    public enum UserRole {
        ADMIN, POLICE
    }
}
