package com.example.mupvehicles.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverIdDto {

    private String id;

    private boolean isSuspended;

    private int numberOfViolationPoints;

    private String picture;

    private OwnerDto owner;
}
