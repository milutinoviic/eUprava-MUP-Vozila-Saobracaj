package com.example.mupvehicles.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuspendDriverIdRequest {

    @NotNull(message = "Value cannot be null")
    private String driverId;

    @NotNull(message = "Value cannot be null")
    private int numberOfViolationPoints;

}
