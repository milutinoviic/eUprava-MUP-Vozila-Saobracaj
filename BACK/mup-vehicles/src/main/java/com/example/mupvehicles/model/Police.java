package com.example.mupvehicles.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Police {

    @Id
    private String id = UUID.randomUUID().toString();

    private String firstName;

    private String lastName;

    private String rank;
}
