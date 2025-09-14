package com.example.mupvehicles.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VerifyVehicleAndOwnerDto {

    @NotNull(message = "Value cannot be null")
    private String registration;

    @NotNull(message = "Value cannot be null")
    private String jmbg;
}
