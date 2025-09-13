package com.example.mupvehicles.service.Impl;

import com.example.mupvehicles.dto.CreateDriverIdDto;
import com.example.mupvehicles.dto.DriverIdDto;
import com.example.mupvehicles.mapper.DriverIdMapper;
import com.example.mupvehicles.model.DriverId;
import com.example.mupvehicles.model.Owner;
import com.example.mupvehicles.repository.DriverIdRepository;
import com.example.mupvehicles.repository.OwnerRepository;
import com.example.mupvehicles.service.DriverIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DriverIdServiceImpl implements DriverIdService {

    private final DriverIdRepository driverIdRepository;
    private final OwnerRepository ownerRepository;
    private final DriverIdMapper driverIdMapper;

    @Autowired
    public DriverIdServiceImpl(DriverIdRepository driverIdRepository, OwnerRepository ownerRepository, DriverIdMapper driverIdMapper) {
        this.driverIdRepository = driverIdRepository;
        this.ownerRepository = ownerRepository;
        this.driverIdMapper = driverIdMapper;
    }

    @Override
    public DriverIdDto createDriverId(CreateDriverIdDto createDriverIdDto) {

        Owner owner = ownerRepository.findByJmbg(createDriverIdDto.getOwnerJmbg());

        if (!ownerRepository.existsByJmbg(owner.getJmbg())) {
            throw new RuntimeException("Owner does not exist");
        }

        if (driverIdRepository.existsByOwner(owner)) {
            throw new RuntimeException("DriverId already exists for this owner");
        }

        DriverId driverId = new DriverId();
        driverId.setId(UUID.randomUUID().toString());
        driverId.setSuspended(false);
        driverId.setNumberOfViolationPoints(0);
        driverId.setPicture("");
        driverId.setOwner(owner);

        driverIdRepository.save(driverId);

        return driverIdMapper.convertDriverIdToDto(driverId);

    }

    @Override
    public DriverIdDto getDriverId(String jmbg) {

        if(!ownerRepository.existsByJmbg(jmbg)) {
            throw new RuntimeException("Owner does not exist");
        }
        DriverId driverId = driverIdRepository.findByOwner_Jmbg(jmbg);

        if(driverId == null) {
            return null;
        }
        return driverIdMapper.convertDriverIdToDto(driverId);

    }

    @Override
    public void deleteDriverId(String driverId) {
        if (!driverIdRepository.existsById(driverId)) {
            throw new RuntimeException("DriverId not found: " + driverId);
        }
        driverIdRepository.deleteById(driverId);
    }


}
