package com.example.mupvehicles.service.Impl;

import com.example.mupvehicles.dto.FineDTO;
import com.example.mupvehicles.dto.PolicePersonDTO;
import com.example.mupvehicles.dto.StatisticDTO;
import com.example.mupvehicles.dto.ViolationDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class PoliceClientService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "http://localhost:8081";

    public List<FineDTO> checkUnpaidFines(String driverId) {
        String url = baseUrl + "/fines/check?driverId=" + driverId;
        ResponseEntity<FineDTO[]> response = restTemplate.getForEntity(url, FineDTO[].class);
        return Arrays.asList(response.getBody());
    }

    public List<ViolationDTO> checkVehicleViolations(String registration) {
        String url = baseUrl + "/violations/check?registration=" + registration;
        ResponseEntity<ViolationDTO[]> response = restTemplate.getForEntity(url, ViolationDTO[].class);
        return Arrays.asList(response.getBody());
    }

    public List<PolicePersonDTO> getAllPoliceOff() {
        String url = baseUrl + "/police/all";
        ResponseEntity<PolicePersonDTO[]> response = restTemplate.getForEntity(url, PolicePersonDTO[].class);
        return Arrays.asList(response.getBody());
    }

    public List<StatisticDTO> getPoliceStatistic(String policeId) {
        String url = baseUrl + "/police/statistics?policeId=" + policeId;
        ResponseEntity<StatisticDTO[]> response = restTemplate.getForEntity(url, StatisticDTO[].class);
        return Arrays.asList(response.getBody());
    }
}
