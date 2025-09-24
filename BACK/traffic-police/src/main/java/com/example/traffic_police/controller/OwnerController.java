package com.example.traffic_police.controller;

import com.example.traffic_police.model.OwnerDTO;
import com.example.traffic_police.model.OwnershipTransferDTO;
import com.example.traffic_police.service.TrafficPoliceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
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
}
