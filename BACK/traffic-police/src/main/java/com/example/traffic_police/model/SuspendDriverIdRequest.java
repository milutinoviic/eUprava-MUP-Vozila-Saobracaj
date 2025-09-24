package com.example.traffic_police.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuspendDriverIdRequest {
    private String driverId;
    private int numberOfViolationPoints;
}