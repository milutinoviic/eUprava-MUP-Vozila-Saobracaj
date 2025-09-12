package com.example.mupvehicles.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Owner {

    @Id
    private String id = UUID.randomUUID().toString();

    private String firstName;

    private String lastName;

    private String address;

    private String jmbg;

    private String email;

}
