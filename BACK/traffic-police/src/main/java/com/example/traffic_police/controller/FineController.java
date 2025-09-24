package com.example.traffic_police.controller;

import com.example.traffic_police.model.Fine;
import com.example.traffic_police.service.TrafficPoliceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fines")
public class FineController {

    @Autowired
    private TrafficPoliceService trafficPoliceService;

    @GetMapping
    public ResponseEntity<List<Fine>> getAllFines() {
        return ResponseEntity.ok(trafficPoliceService.getAllFines());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateFine(@PathVariable String id) {
        trafficPoliceService.markFineAsPaid(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unpaid/{jmbg}")
    public ResponseEntity<List<Fine>> getUnpaidFines(@PathVariable String jmbg) {
        return ResponseEntity.ok(trafficPoliceService.findUnpaidFinesByDriverId(jmbg));
    }

}
