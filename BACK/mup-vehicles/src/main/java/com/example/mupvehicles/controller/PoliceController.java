package com.example.mupvehicles.controller;

import com.example.mupvehicles.dto.FineDTO;
import com.example.mupvehicles.dto.PolicePersonDTO;
import com.example.mupvehicles.dto.StatisticDTO;
import com.example.mupvehicles.dto.ViolationDTO;
import com.example.mupvehicles.service.Impl.PoliceClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/police")
public class PoliceController {

    private final PoliceClientService policeClientService;

    @Autowired
    public PoliceController(PoliceClientService policeClientService) {
        this.policeClientService = policeClientService;
    }

    @GetMapping("/fines/{driverId}")
    public List<FineDTO> getUnpaidFines(@PathVariable String driverId) {
        return policeClientService.checkUnpaidFines(driverId);
    }

    @GetMapping("/violations/{registration}")
    public List<ViolationDTO> getVehicleViolations(@PathVariable String registration) {
        return policeClientService.checkVehicleViolations(registration);
    }

    @GetMapping("/officers")
    public List<PolicePersonDTO> getAllOfficers() {
        return policeClientService.getAllPoliceOff();
    }

    @GetMapping("/statistics/{policeId}")
    public List<StatisticDTO> getStatistics(@PathVariable String policeId) {
        return policeClientService.getPoliceStatistic(policeId);
    }

}
