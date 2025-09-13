package com.example.mupvehicles.service;

import com.example.mupvehicles.dto.CreateDriverIdDto;
import com.example.mupvehicles.dto.DriverIdDto;

public interface DriverIdService {

    DriverIdDto createDriverId(CreateDriverIdDto createDriverIdDto);

    DriverIdDto getDriverId(String jmbg);

    void deleteDriverId(String driverId);

}
