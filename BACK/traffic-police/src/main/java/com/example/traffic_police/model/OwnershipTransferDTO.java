package com.example.traffic_police.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnershipTransferDTO {
    private String id;
    private VehicleDTO vehicle;
    private OwnerDTO ownerOld;
    private OwnerDTO ownerNew;
    private LocalDateTime dateOfTransfer;
}