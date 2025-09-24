package com.example.traffic_police.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SearchVehicleRequest {
    private String mark;
    private String model;
    private String color;
    private String registration;
}
