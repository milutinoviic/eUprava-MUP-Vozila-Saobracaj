package com.example.mupvehicles.service.Impl;

import com.example.mupvehicles.dto.CreateDriverIdDto;
import com.example.mupvehicles.dto.DriverIdDto;
import com.example.mupvehicles.dto.SuspendDriverIdRequest;
import com.example.mupvehicles.mapper.DriverIdMapper;
import com.example.mupvehicles.model.DriverId;
import com.example.mupvehicles.model.Owner;
import com.example.mupvehicles.repository.DriverIdRepository;
import com.example.mupvehicles.repository.OwnerRepository;
import com.example.mupvehicles.service.DriverIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    public Resource getDriverIdPicture(String userJmbg) {
        Owner owner = ownerRepository.findByJmbg(userJmbg);

        if (owner == null) {
            throw new RuntimeException("Owner not found");
        }

        DriverId driverId = driverIdRepository.findByOwner(owner)
                .orElseThrow(() -> new RuntimeException("DriverId not found for this owner"));

        try {
            Path filePath = Paths.get("src/main/resources" + driverId.getPicture()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new RuntimeException("File not found: " + driverId.getPicture());
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to load image", e);
        }
    }

    @Override
    public DriverIdDto createDriverId(CreateDriverIdDto createDriverIdDto) {

        Owner owner = ownerRepository.findByJmbg(createDriverIdDto.getOwnerJmbg());

        if (owner == null) {
            throw new RuntimeException("Owner does not exist");
        }

        if (driverIdRepository.existsByOwner(owner)) {
            throw new RuntimeException("DriverId already exists for this owner");
        }

        MultipartFile file = createDriverIdDto.getPicture();

        try {

            String uploadDir = "src/main/resources/Images";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();


            String imageName = "driver_" + owner.getJmbg() + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, imageName).normalize();


            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            DriverId driverId = new DriverId();
            driverId.setId(UUID.randomUUID().toString());
            driverId.setSuspended(false);
            driverId.setNumberOfViolationPoints(0);
            driverId.setPicture("/Images/" + imageName);
            driverId.setOwner(owner);

            driverIdRepository.save(driverId);

            return driverIdMapper.convertDriverIdToDto(driverId);

        } catch (IOException e) {
            throw new RuntimeException("Failed to save picture", e);
        }
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

    @Override
    public DriverIdDto suspendDriverId(SuspendDriverIdRequest suspendDriverIdRequest){

        DriverId driverId = driverIdRepository.findById(suspendDriverIdRequest.getDriverId())
                .orElseThrow(() -> new RuntimeException("DriverId not found: " + suspendDriverIdRequest.getDriverId()));

        int numberOfViolationPoints = driverId.getNumberOfViolationPoints() + suspendDriverIdRequest.getNumberOfViolationPoints();

        driverId.setNumberOfViolationPoints(numberOfViolationPoints);

        if(numberOfViolationPoints >= 9){
            driverId.setSuspended(true);
        }
        driverIdRepository.save(driverId);

        return driverIdMapper.convertDriverIdToDto(driverId);

    }

    @Override
    public DriverIdDto reactivateDriverId(String id){

        DriverId driverId = driverIdRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DriverId not found: " + id));

        driverId.setNumberOfViolationPoints(0);
        driverId.setSuspended(false);

        driverIdRepository.save(driverId);
        return driverIdMapper.convertDriverIdToDto(driverId);


    }


}
