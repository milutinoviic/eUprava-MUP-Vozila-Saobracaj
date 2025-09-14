package com.example.mupvehicles.mapper;

import com.example.mupvehicles.dto.OwnershipTransferDto;
import com.example.mupvehicles.dto.VehicleDto;
import com.example.mupvehicles.model.OwnershipTransfer;
import com.example.mupvehicles.model.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<OwnershipTransferDto> convertTransferOwnershipListToTransferOwnershipDtoList(List<OwnershipTransfer> ownershipTransferList) {
        return ownershipTransferList.stream()
                .map(this::convertTransferOwnershipToTransferOwnershipDto)
                .collect(Collectors.toList());
    }
}
