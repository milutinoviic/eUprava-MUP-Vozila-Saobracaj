package com.example.mupvehicles.service.Impl;

import com.example.mupvehicles.dto.CreateOwnerDto;
import com.example.mupvehicles.dto.OwnerDto;
import com.example.mupvehicles.mapper.OwnerMapper;
import com.example.mupvehicles.model.Owner;
import com.example.mupvehicles.repository.OwnerRepository;
import com.example.mupvehicles.repository.VehicleRepository;
import com.example.mupvehicles.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;
    private final VehicleRepository vehicleRepository;
    private final OwnerMapper ownerMapper;

    @Autowired
    public OwnerServiceImpl(OwnerRepository ownerRepository,VehicleRepository vehicleRepository,OwnerMapper ownerMapper) {
        this.ownerRepository = ownerRepository;
        this.vehicleRepository = vehicleRepository;
        this.ownerMapper = ownerMapper;
    }

    @Override
    public OwnerDto getOwnerByRegistration(String registration) {

        Owner owner = vehicleRepository.findOwnerByRegistration(registration);
        return ownerMapper.convertOwnerToOwnerDto(owner);

    }

    @Override
    public OwnerDto createOwner(CreateOwnerDto createOwnerDto) {

        if(ownerRepository.existsByEmail(createOwnerDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if(ownerRepository.existsByJmbg(createOwnerDto.getJmbg())) {
            throw new RuntimeException("Owner already exists");
        }

        Owner owner = new Owner();

        owner.setId(UUID.randomUUID().toString());
        owner.setFirstName(createOwnerDto.getFirstName());
        owner.setLastName(createOwnerDto.getLastName());
        owner.setAddress(createOwnerDto.getAddress());
        owner.setEmail(createOwnerDto.getEmail());
        owner.setJmbg(createOwnerDto.getJmbg());

        ownerRepository.save(owner);

        return ownerMapper.convertOwnerToOwnerDto(owner);

    }

}
