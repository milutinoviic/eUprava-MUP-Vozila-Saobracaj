package com.example.traffic_police.controller;

import com.example.traffic_police.model.NewViolationRequest;
import com.example.traffic_police.model.OwnerDTO;
import com.example.traffic_police.model.Violation;
import com.example.traffic_police.service.TrafficPoliceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/violations")
public class ViolationController {

    @Autowired
    private TrafficPoliceService trafficPoliceService;

    @GetMapping
    public ResponseEntity<List<Violation>> getAllViolations() {
        List<Violation> violations = trafficPoliceService.getAllViolations();
        return ResponseEntity.ok(violations);
    }

    @GetMapping("/{policeId}")
    public ResponseEntity<List<Violation>> getViolationsByPolice(@PathVariable String policeId) {
        return ResponseEntity.ok(trafficPoliceService.getAssignedViolations(policeId));
    }

    @GetMapping("/history/{driverId}")
    public ResponseEntity<List<Violation>> getViolationsByDriver(@PathVariable String driverId) {
        return ResponseEntity.ok(trafficPoliceService.getViolationHistory(driverId));
    }

    @GetMapping("/{format}/{period}")
    public ResponseEntity<byte[]> getViolationsByFormat(@PathVariable String format, @PathVariable String period) {
        return ResponseEntity.ok(trafficPoliceService.exportViolationData(format, period));
    }

    @PostMapping
    public ResponseEntity<List<OwnerDTO>> addViolation(@RequestBody NewViolationRequest violation) {
        return ResponseEntity.ok(trafficPoliceService.handleNewViolation(violation));
    }

    @PatchMapping("/assign/{officerId}/{violationId}")
    public ResponseEntity<Void> assignViolation(@PathVariable String officerId, @PathVariable String violationId) {
        trafficPoliceService.assignOfficerToViolation(violationId, officerId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history/{vehicleId}")
    public ResponseEntity<List<Violation>> getViolationsByVehicle(@PathVariable String vehicleId) {
        return ResponseEntity.ok(trafficPoliceService.checkVehicleViolations(vehicleId));
    }
}
