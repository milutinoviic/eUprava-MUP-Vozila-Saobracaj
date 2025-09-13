package com.example.mupvehicles.dto;

import lombok.Data;

@Data
public class VehicleSearchRequest {
    private String mark;
    private String model;
    private String color;
    private String registration;
}
