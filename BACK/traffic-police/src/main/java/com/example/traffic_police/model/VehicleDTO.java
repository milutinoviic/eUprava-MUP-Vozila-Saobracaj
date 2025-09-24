package com.example.traffic_police.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {
    private String id;
    private String mark;
    private String model;
    private String registration;
    private int year;
    private String color;
    private boolean isStolen;
    private String ownerId;
}