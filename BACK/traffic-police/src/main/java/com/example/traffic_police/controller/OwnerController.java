package com.example.traffic_police.controller;

import com.example.traffic_police.model.*;
import com.example.traffic_police.model.OwnershipTransferDTO;
import com.example.traffic_police.service.TrafficPoliceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/api/owners")
public class OwnerController {

    @Autowired
    private TrafficPoliceService trafficPoliceService;

    @GetMapping
    public ResponseEntity<List<OwnerDTO>> getOwners() {
        return ResponseEntity.ok(trafficPoliceService.fetchAllDrivers());
    }

    @GetMapping("/history/{registration}")
    public ResponseEntity<List<OwnershipTransferDTO>> getHistory(@PathVariable String registration) {
        return ResponseEntity.ok(trafficPoliceService.getOwnershipHistoryForInvestigation(registration));
    }

    @GetMapping("/id/{jmbg}")
    public ResponseEntity<DriverIDDTO> getDriverID(@PathVariable String jmbg) {
        return ResponseEntity.ok(trafficPoliceService.searchDriverIDByDriverId(jmbg));
    }
}
