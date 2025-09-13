package com.example.mupvehicles.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOwnershipTransferDto {

    private String vehicleId;

    private String oldOwnerId;

    private String  newOwnerId;

}
