package com.example.mupvehicles.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ViolationDTO {
    private String id;
    private TypeOfViolation typeOfViolation;
    private LocalDateTime date;
    private String location;
    private String driverId;
    private String vehicleId;
    private String policeId;

    public enum TypeOfViolation {
        MINOR, MAJOR, CRITICAL
    }
}