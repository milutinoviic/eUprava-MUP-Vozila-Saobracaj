package com.example.traffic_police.controller;

import com.example.traffic_police.model.PolicePerson;
import com.example.traffic_police.model.StatisticDTO;
import com.example.traffic_police.service.TrafficPoliceService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/police")
public class PoliceController {

    @Autowired
    private TrafficPoliceService trafficPoliceService;


    @GetMapping
    public ResponseEntity<List<PolicePerson>> GetAllPolice() {
        return ResponseEntity.ok(trafficPoliceService.getAllPolice());
    }

    @PostMapping
    public ResponseEntity<PolicePerson> AddPolice(@RequestBody PolicePerson p) {
        PolicePerson inserted = trafficPoliceService.insertPolicePerson(p);
        return ResponseEntity.ok(inserted);
    }

    @PatchMapping("/suspend/{id}")
    public ResponseEntity<Void> SuspendPolice(@PathVariable String id) {
        if (id.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        trafficPoliceService.suspendOfficer(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/promotion/{id}")
    public ResponseEntity<Void> PromotePolice(@PathVariable String id) {
        if (id.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        trafficPoliceService.promoteOfficer(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/statistics/{id}")
    public ResponseEntity<List<StatisticDTO>> GetStatistics(@PathVariable String id) {
        return ResponseEntity.ok(trafficPoliceService.getDailyStatistics(id));
    }



}
