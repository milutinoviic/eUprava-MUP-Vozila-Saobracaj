package com.example.mupvehicles.mapper;

import com.example.mupvehicles.dto.VehicleDto;
import com.example.mupvehicles.model.Owner;
import com.example.mupvehicles.model.Vehicle;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class VehicleMapper {

    public VehicleDto converttoVehicleDto(Vehicle vehicle) {

        VehicleDto vehicleDto = new VehicleDto();

        vehicleDto.setId(vehicle.getId());
        vehicleDto.setMark(vehicle.getMark());
        vehicleDto.setModel(vehicle.getModel());
        vehicleDto.setRegistration(vehicle.getRegistration());
        vehicleDto.setYear(vehicle.getYear());
        vehicleDto.setColor(vehicle.getColor());
        vehicleDto.setStolen(vehicle.isStolen());
        vehicleDto.setOwnerId(vehicle.getOwner().getId());

        return vehicleDto;
    }

    public List<VehicleDto> convertToVehicleDtoList(List<Vehicle> vehicles) {
        return vehicles.stream()
                .map(this::converttoVehicleDto)
                .collect(Collectors.toList());
    }
}
