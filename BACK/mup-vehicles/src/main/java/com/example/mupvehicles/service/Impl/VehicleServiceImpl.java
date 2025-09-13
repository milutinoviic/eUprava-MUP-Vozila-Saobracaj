package com.example.mupvehicles.service.Impl;

import com.example.mupvehicles.dto.VehicleDto;
import com.example.mupvehicles.dto.VehicleSearchRequest;
import com.example.mupvehicles.mapper.VehicleMapper;
import com.example.mupvehicles.model.Vehicle;
import com.example.mupvehicles.repository.VehicleRepository;
import com.example.mupvehicles.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;


    @Autowired
    public VehicleServiceImpl(VehicleRepository vehicleRepository, VehicleMapper vehicleMapper) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
    }

    @Override
    public List<VehicleDto> findAllVehiclesByOwnerJmbg(String ownerJmbg) {

        List<Vehicle> vehicleList = vehicleRepository.findByOwnerJmbg(ownerJmbg);
        return vehicleMapper.convertToVehicleDtoList(vehicleList);

    }

    @Override
    public List<VehicleDto> searchVehicles(VehicleSearchRequest vehicleSearchRequest) {
        List<Vehicle> vehicleList =  vehicleRepository.searchVehicles(vehicleSearchRequest.getMark(),vehicleSearchRequest.getModel(),vehicleSearchRequest.getColor(),vehicleSearchRequest.getRegistration());
        return vehicleMapper.convertToVehicleDtoList(vehicleList);
    }

    @Override
    public VehicleDto findVehicleByRegistration(String registration) {
        Vehicle vehicle = vehicleRepository.findVehicleByRegistration(registration);
        if (vehicle == null) {
            return null;
        }
        return vehicleMapper.converttoVehicleDto(vehicle);
    }


}
