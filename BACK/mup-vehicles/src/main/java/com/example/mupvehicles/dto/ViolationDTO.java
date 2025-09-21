package com.example.mupvehicles.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ViolationDTO {
    private String id;
    private String type_of_violation;
    private LocalDateTime date;
    private String location;
    private String driverId;
    private String vehicleId;
    private String policeId;
}