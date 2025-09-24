package com.example.traffic_police.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverIDDTO {
    private String id;
    private String ownerID;
    private boolean isSuspended;
    private int numberOfViolations;
}