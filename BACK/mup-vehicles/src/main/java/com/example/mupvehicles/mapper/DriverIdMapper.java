package com.example.mupvehicles.mapper;

import com.example.mupvehicles.dto.DriverIdDto;
import com.example.mupvehicles.model.DriverId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DriverIdMapper {

    private final OwnerMapper ownerMapper;

    @Autowired
    public DriverIdMapper(OwnerMapper ownerMapper) {
        this.ownerMapper = ownerMapper;
    }

    public DriverIdDto convertDriverIdToDto(DriverId driverId) {

        DriverIdDto dto = new DriverIdDto();

        dto.setId(driverId.getId());
        dto.setSuspended(driverId.isSuspended());
        dto.setNumberOfViolationPoints(driverId.getNumberOfViolationPoints());
        dto.setPicture(driverId.getPicture());

        if (driverId.getOwner() != null) {
            dto.setOwner(ownerMapper.convertOwnerToOwnerDto(driverId.getOwner()));
        }

        return dto;
    }

}
