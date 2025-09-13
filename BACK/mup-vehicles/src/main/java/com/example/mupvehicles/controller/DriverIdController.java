package com.example.mupvehicles.controller;

import com.example.mupvehicles.dto.CreateDriverIdDto;
import com.example.mupvehicles.dto.DriverIdDto;
import com.example.mupvehicles.service.DriverIdService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/driverIds")
public class DriverIdController {

    private final DriverIdService driverIdService;

    @Autowired
    public DriverIdController(DriverIdService driverIdService) {
        this.driverIdService = driverIdService;
    }

    @PostMapping
    public DriverIdDto createDriverId(@Valid @RequestBody CreateDriverIdDto createDriverIdDto) {
        return driverIdService.createDriverId(createDriverIdDto);
    }

}
