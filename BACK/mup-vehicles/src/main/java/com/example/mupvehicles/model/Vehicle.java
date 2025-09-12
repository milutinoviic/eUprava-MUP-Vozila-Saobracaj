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
public class Vehicle {

    @Id
    private String id = UUID.randomUUID().toString();

    private String mark;

    private String model;

    private String registration;

    private int year;

    private String color;

    private boolean isStolen;

    @ManyToOne
    private Owner owner;

}
