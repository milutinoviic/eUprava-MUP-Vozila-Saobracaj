package com.example.traffic_police.controller;

import com.example.traffic_police.model.SearchVehicleRequest;
import com.example.traffic_police.model.VehicleDTO;
import com.example.traffic_police.model.VehicleVerificationRequest;
import com.example.traffic_police.model.Violation;
import com.example.traffic_police.service.TrafficPoliceService;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired
    private TrafficPoliceService service;


    @GetMapping("/stolen/{registration}")
    public ResponseEntity<String> checkIfStolen(@PathVariable String registration) {
        return ResponseEntity.ok(service.checkVehicleStatus(registration));
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyVehicle(@RequestBody VehicleVerificationRequest req) {
        return ResponseEntity.ok(service.verifyVehicle(req));
    }

    @PostMapping("/stolen/{registration}")
    public ResponseEntity<VehicleDTO> reportAsStolen(@PathVariable String registration) {
        return ResponseEntity.ok(service.reportVehicleAsStolen(registration));
    }

    @PostMapping("/search")
    public ResponseEntity<List<VehicleDTO>> search(@RequestBody SearchVehicleRequest req) {
        return ResponseEntity.ok(service.searchVehicleByOptional(req));
    }


}
