package com.example.mupvehicles.service;

import com.example.mupvehicles.dto.CreateVehicleDto;
import com.example.mupvehicles.dto.VehicleDto;
import com.example.mupvehicles.dto.VehicleSearchRequest;
import com.example.mupvehicles.dto.VerifyVehicleAndOwnerDto;

import java.util.List;

public interface VehicleService {

    List<VehicleDto> findAllVehiclesByOwnerJmbg(String ownerJmbg);

    List<VehicleDto> searchVehicles(VehicleSearchRequest vehicleSearchRequest);

    VehicleDto findVehicleByRegistration(String registration);

    VehicleDto createVehicle(CreateVehicleDto createVehicleDto);

    String isVehicleStolen(String registration);

    VehicleDto reportVehicleStolen(String registration);

    String verifyVehicleAndOwner(VerifyVehicleAndOwnerDto verifyVehicleAndOwnerDto);

}
