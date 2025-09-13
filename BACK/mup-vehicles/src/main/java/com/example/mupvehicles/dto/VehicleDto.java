package com.example.mupvehicles.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDto {

    private String id;

    private String mark;

    private String model;

    private String registration;

    private int year;

    private String color;

    private boolean isStolen;

    private String ownerId;
}
