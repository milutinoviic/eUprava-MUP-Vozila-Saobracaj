package com.example.mupvehicles.service.Impl;

import com.example.mupvehicles.dto.CreateOwnerDto;
import com.example.mupvehicles.dto.OwnerDto;
import com.example.mupvehicles.mapper.OwnerMapper;
import com.example.mupvehicles.model.DriverId;
import com.example.mupvehicles.model.Owner;
import com.example.mupvehicles.model.OwnershipTransfer;
import com.example.mupvehicles.model.Vehicle;
import com.example.mupvehicles.repository.DriverIdRepository;
import com.example.mupvehicles.repository.OwnerRepository;
import com.example.mupvehicles.repository.OwnershipTransferRepository;
import com.example.mupvehicles.repository.VehicleRepository;
import com.example.mupvehicles.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;
    private final VehicleRepository vehicleRepository;
    private final OwnerMapper ownerMapper;
    private final DriverIdRepository driverIdRepository;
    private final OwnershipTransferRepository ownershipTransferRepository;

    @Autowired
    public OwnerServiceImpl(OwnerRepository ownerRepository, VehicleRepository vehicleRepository, OwnerMapper ownerMapper, DriverIdRepository driverIdRepository, OwnershipTransferRepository ownershipTransferRepository) {
        this.ownerRepository = ownerRepository;
        this.vehicleRepository = vehicleRepository;
        this.ownerMapper = ownerMapper;
        this.driverIdRepository = driverIdRepository;
        this.ownershipTransferRepository = ownershipTransferRepository;
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

    @Override
    public void deleteOwner(String ownerId) {

        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found: " + ownerId));


        List<Vehicle> vehicleList = vehicleRepository.findByOwnerJmbg(owner.getJmbg());

        if(!vehicleList.isEmpty()) {
            throw new RuntimeException("Can not delete owner, owner has vehicles");
        }

        Optional<DriverId> driverId = driverIdRepository.findByOwner(owner);
        if (driverId.isPresent()) {
            throw new RuntimeException("Can not delete owner, driver has driverId");
        }

        Optional<OwnershipTransfer> ownershipTransferByNewOwner = ownershipTransferRepository.findByNewOwner(owner);

        if (ownershipTransferByNewOwner.isPresent()) {
            throw new RuntimeException("Can not delete owner, owner has ownershipTransfer");
        }
        Optional<OwnershipTransfer> ownershipTransferByOldOwner = ownershipTransferRepository.findByOldOwner(owner);

        if (ownershipTransferByOldOwner.isPresent()) {
            throw new RuntimeException("Can not delete owner, owner has ownershipTransfer");
        }

        ownerRepository.delete(owner);
    }

}
