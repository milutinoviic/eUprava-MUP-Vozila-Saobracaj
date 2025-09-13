package com.example.mupvehicles.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DriverId {

    @Id
    private String id = UUID.randomUUID().toString();

    private boolean isSuspended;

    private int numberOfViolationPoints;

    private String picture;

    @ManyToOne
    private Owner owner;

}
