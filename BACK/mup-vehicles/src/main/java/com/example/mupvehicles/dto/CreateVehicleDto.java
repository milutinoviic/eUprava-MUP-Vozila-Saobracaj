package com.example.mupvehicles.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateVehicleDto {

    @NotNull(message = "Value cannot be null")
    private String mark;

    @NotNull(message = "Value cannot be null")
    private String model;

    @NotNull(message = "Value cannot be null")
    private String registration;

    @NotNull(message = "Value cannot be null")
    private int year;

    @NotNull(message = "Value cannot be null")
    private String color;

    @NotNull(message = "Value cannot be null")
    private String ownerJmbg;

}
