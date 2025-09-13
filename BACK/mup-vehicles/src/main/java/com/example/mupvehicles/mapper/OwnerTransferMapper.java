package com.example.mupvehicles.mapper;

import com.example.mupvehicles.dto.OwnershipTransferDto;
import com.example.mupvehicles.model.OwnershipTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OwnerTransferMapper {

    private OwnerMapper ownerMapper;
    private VehicleMapper vehicleMapper;

    @Autowired
    public OwnerTransferMapper(OwnerMapper ownerMapper, VehicleMapper vehicleMapper) {
        this.ownerMapper = ownerMapper;
        this.vehicleMapper = vehicleMapper;
    }

    public OwnershipTransferDto convertTransferOwnershipToTransferOwnershipDto(OwnershipTransfer ownershipTransfer) {

        OwnershipTransferDto ownershipTransferDto = new OwnershipTransferDto();
        ownershipTransferDto.setId(ownershipTransfer.getId());
        ownershipTransferDto.setVehicle(vehicleMapper.converttoVehicleDto(ownershipTransfer.getVehicle()));
        ownershipTransferDto.setOwnerOld(ownerMapper.convertOwnerToOwnerDto(ownershipTransfer.getOldOwner()));
        ownershipTransferDto.setOwnerNew(ownerMapper.convertOwnerToOwnerDto(ownershipTransfer.getNewOwner()));
        ownershipTransferDto.setDateOfTransfer(ownershipTransfer.getDateOfTransfer());
        return ownershipTransferDto;
    }
}
