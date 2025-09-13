package com.example.mupvehicles.controller;

import com.example.mupvehicles.dto.CreateDriverIdDto;
import com.example.mupvehicles.dto.DriverIdDto;
import com.example.mupvehicles.service.DriverIdService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{jmbg}")
    public DriverIdDto getDriverIdByOwner(@PathVariable String jmbg) {
        return driverIdService.getDriverId(jmbg);
    }

    @DeleteMapping("/deleteDriverId/{id}")
    public ResponseEntity<Void> deleteDriverId(@PathVariable String id) {
        driverIdService.deleteDriverId(id);
        return ResponseEntity.ok().build();
    }


}
