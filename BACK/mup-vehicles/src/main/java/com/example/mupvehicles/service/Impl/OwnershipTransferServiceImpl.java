package com.example.mupvehicles.service.Impl;

import com.example.mupvehicles.dto.CreateOwnershipTransferDto;
import com.example.mupvehicles.dto.OwnershipTransferDto;
import com.example.mupvehicles.mapper.OwnerTransferMapper;
import com.example.mupvehicles.model.Owner;
import com.example.mupvehicles.model.OwnershipTransfer;
import com.example.mupvehicles.model.Vehicle;
import com.example.mupvehicles.repository.OwnerRepository;
import com.example.mupvehicles.repository.OwnershipTransferRepository;
import com.example.mupvehicles.repository.VehicleRepository;
import com.example.mupvehicles.service.OwnershipTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class OwnershipTransferServiceImpl implements OwnershipTransferService {

    private final OwnershipTransferRepository ownershipTransferRepository;
    private final VehicleRepository vehicleRepository;
    private final OwnerRepository ownerRepository;
    private final OwnerTransferMapper ownerTransferMapper;

    @Autowired
    public OwnershipTransferServiceImpl(OwnershipTransferRepository ownershipTransferRepository, VehicleRepository vehicleRepository, OwnerRepository ownerRepository,OwnerTransferMapper ownerTransferMapper) {
        this.ownershipTransferRepository = ownershipTransferRepository;
        this.vehicleRepository = vehicleRepository;
        this.ownerRepository = ownerRepository;
        this.ownerTransferMapper = ownerTransferMapper;
    }

    @Override
    public OwnershipTransferDto transferOwnership(CreateOwnershipTransferDto transferOwnershipDto) {

        Vehicle vehicle = vehicleRepository.findById(transferOwnershipDto.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vozilo ne postoji"));

        Owner oldOwner = ownerRepository.findById(transferOwnershipDto.getOldOwnerId())
                .orElseThrow(() -> new RuntimeException("Stari vlasnik ne postoji"));
        Owner newOwner = ownerRepository.findById(transferOwnershipDto.getNewOwnerId())
                .orElseThrow(() -> new RuntimeException("Novi vlasnik ne postoji"));

        vehicle.setOwner(newOwner);
        vehicleRepository.save(vehicle);

        OwnershipTransfer transfer = new OwnershipTransfer();
        transfer.setId(UUID.randomUUID().toString());
        transfer.setVehicle(vehicle);
        transfer.setOldOwner(oldOwner);
        transfer.setNewOwner(newOwner);
        transfer.setDateOfTransfer(new Date());

        ownershipTransferRepository.save(transfer);

        return ownerTransferMapper.convertTransferOwnershipToTransferOwnershipDto(transfer);





    }


}
