package com.example.mupvehicles.service;

import com.example.mupvehicles.dto.VehicleDto;
import com.example.mupvehicles.dto.VehicleSearchRequest;

import java.util.List;

public interface VehicleService {

    List<VehicleDto> findAllVehiclesByOwnerJmbg(String ownerJmbg);

    List<VehicleDto> searchVehicles(VehicleSearchRequest vehicleSearchRequest);

}
