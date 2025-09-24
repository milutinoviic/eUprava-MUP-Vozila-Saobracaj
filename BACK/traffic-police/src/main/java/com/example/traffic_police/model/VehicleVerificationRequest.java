package com.example.traffic_police.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class VehicleVerificationRequest {
    private String registration;
    private String jmbg;
}
