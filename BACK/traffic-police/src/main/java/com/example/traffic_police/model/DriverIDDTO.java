package com.example.traffic_police.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverIDDTO {

    private String id;

    private boolean isSuspended;

    private int numberOfViolationPoints;

    private String picture;

    private OwnerDTO owner;
}