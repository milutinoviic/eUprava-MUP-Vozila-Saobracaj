package com.example.traffic_police.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class NewViolationRequest {
    private Violation violation;
    private DriverIDDTO driverId;

}
