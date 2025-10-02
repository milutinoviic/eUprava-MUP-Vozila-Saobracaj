package com.example.mupvehicles.service;

import com.example.mupvehicles.dto.CreateDriverIdDto;
import com.example.mupvehicles.dto.DriverIdDto;
import com.example.mupvehicles.dto.SuspendDriverIdRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;


public interface DriverIdService {

    DriverIdDto createDriverId(CreateDriverIdDto createDriverIdDto);

    DriverIdDto getDriverId(String jmbg);

    void deleteDriverId(String driverId);

    DriverIdDto suspendDriverId(SuspendDriverIdRequest suspendDriverIdRequest);

    DriverIdDto reactivateDriverId(String id);

    Resource getDriverIdPicture(String userJmbg);

}
