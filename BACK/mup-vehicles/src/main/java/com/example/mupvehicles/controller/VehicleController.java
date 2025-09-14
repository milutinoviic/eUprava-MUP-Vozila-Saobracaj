package com.example.mupvehicles.controller;

import com.example.mupvehicles.dto.CreateVehicleDto;
import com.example.mupvehicles.dto.VehicleDto;
import com.example.mupvehicles.dto.VehicleSearchRequest;
import com.example.mupvehicles.dto.VerifyVehicleAndOwnerDto;
import com.example.mupvehicles.service.VehicleService;
import jakarta.validation.Valid;
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

    @GetMapping("/getVehiclesByRegistration/{jmbg}")
    public ResponseEntity<List<VehicleDto>> getAllVehiclesByOwnerJmbg(@PathVariable String jmbg) {
        return new ResponseEntity<>(vehicleService.findAllVehiclesByOwnerJmbg(jmbg), HttpStatus.OK);
    }

    @GetMapping("/isStolen/{id}")
    public String isStolen(@PathVariable String id) {
        return vehicleService.isVehicleStolen(id);
    }

    @PostMapping("/search")
    public List<VehicleDto> searchVehicles(@RequestBody VehicleSearchRequest request) {
        return vehicleService.searchVehicles(request);
    }

    @GetMapping("/registration/{registration}")
    public VehicleDto findVehicleByRegistration(@PathVariable String registration) {
        return vehicleService.findVehicleByRegistration(registration);
    }

    @PostMapping("/createVehicle")
    public VehicleDto createVehicle(@Valid @RequestBody CreateVehicleDto createVehicleDto) {
        return vehicleService.createVehicle(createVehicleDto);
    }

    @PostMapping("/{registration}/report-stolen")
    public VehicleDto reportVehicleStolen(@PathVariable String registration) {
        return vehicleService.reportVehicleStolen(registration);
    }

    @PostMapping("/verify")
    public String verifyVehicleAndOwner(@Valid @RequestBody VerifyVehicleAndOwnerDto dto) {
        return vehicleService.verifyVehicleAndOwner(dto);
    }

}
