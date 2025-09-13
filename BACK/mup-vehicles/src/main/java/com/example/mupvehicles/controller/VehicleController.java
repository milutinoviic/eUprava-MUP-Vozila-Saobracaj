package com.example.mupvehicles.controller;

import com.example.mupvehicles.dto.VehicleDto;
import com.example.mupvehicles.dto.VehicleSearchRequest;
import com.example.mupvehicles.model.Vehicle;
import com.example.mupvehicles.service.VehicleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    @Autowired
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping("/{jmbg}")
    public ResponseEntity<List<VehicleDto>> getAllVehiclesByOwnerJmbg(@PathVariable String jmbg) {
        return new ResponseEntity<>(vehicleService.findAllVehiclesByOwnerJmbg(jmbg), HttpStatus.OK);
    }

    @PostMapping("/search")
    public List<VehicleDto> searchVehicles(@RequestBody VehicleSearchRequest request) {
        return vehicleService.searchVehicles(request);
    }

}
