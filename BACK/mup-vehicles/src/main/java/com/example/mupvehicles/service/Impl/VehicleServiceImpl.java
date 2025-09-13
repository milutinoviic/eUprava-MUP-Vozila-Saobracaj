package com.example.mupvehicles.service.Impl;

import com.example.mupvehicles.dto.CreateVehicleDto;
import com.example.mupvehicles.dto.VehicleDto;
import com.example.mupvehicles.dto.VehicleSearchRequest;
import com.example.mupvehicles.mapper.VehicleMapper;
import com.example.mupvehicles.model.Owner;
import com.example.mupvehicles.model.Vehicle;
import com.example.mupvehicles.repository.OwnerRepository;
import com.example.mupvehicles.repository.VehicleRepository;
import com.example.mupvehicles.service.VehicleService;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;
    private final OwnerRepository ownerRepository;


    @Autowired
    public VehicleServiceImpl(VehicleRepository vehicleRepository, VehicleMapper vehicleMapper,OwnerRepository ownerRepository) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
        this.ownerRepository = ownerRepository;
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

    @Override
    public VehicleDto createVehicle(CreateVehicleDto createVehicleDto) {

        if(!ownerRepository.existsByJmbg(createVehicleDto.getOwnerJmbg())){
            throw new RuntimeException("Owner does not exist");
        }

        if(vehicleRepository.existsByRegistration(createVehicleDto.getRegistration())){

            throw new RuntimeException("Registration exist");
        }

        Owner owner = ownerRepository.findByJmbg(createVehicleDto.getOwnerJmbg());

        Vehicle vehicle = new Vehicle();
        vehicle.setId(UUID.randomUUID().toString());
        vehicle.setMark(createVehicleDto.getMark());
        vehicle.setModel(createVehicleDto.getModel());
        vehicle.setRegistration(createVehicleDto.getRegistration());
        vehicle.setYear(createVehicleDto.getYear());
        vehicle.setColor(createVehicleDto.getColor());
        vehicle.setStolen(false);
        vehicle.setOwner(owner);

        vehicleRepository.save(vehicle);

        return vehicleMapper.converttoVehicleDto(vehicle);

    }


}
