package com.example.mupvehicles.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OwnershipTransferDto {

    private String Id;

    private VehicleDto vehicle;

    private OwnerDto ownerOld;

    private OwnerDto ownerNew;

    private Date dateOfTransfer;


}
