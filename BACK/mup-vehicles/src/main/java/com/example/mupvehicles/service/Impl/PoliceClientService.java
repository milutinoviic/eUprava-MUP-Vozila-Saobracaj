package com.example.mupvehicles.service.Impl;

import com.example.mupvehicles.dto.*;
import com.example.mupvehicles.model.Vehicle;
import com.example.mupvehicles.service.VehicleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class PoliceClientService {


    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private VehicleService vehicleService;

    @Value("${traficePolice.service.url}")
    private String baseUrl;

    public List<FineDTO> checkUnpaidFines(String driverId) {
        String url = baseUrl + "/fines/unpaid/" + driverId;
        ResponseEntity<FineDTO[]> response = restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.GET,
                getVoidHttpEntity(),
                FineDTO[].class
        );
        return Arrays.asList(response.getBody());
    }

    public List<ViolationDTO> checkVehicleViolations(String registration) {
        VehicleDto vehicle = vehicleService.findVehicleByRegistration(registration);
        String url = baseUrl + "/violations/history/vehicle/" + vehicle.getId();
        ResponseEntity<ViolationDTO[]> response = restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.GET,
                getVoidHttpEntity(),
                ViolationDTO[].class
        );
        return Arrays.asList(response.getBody());
    }

    public List<PolicePersonDTO> getAllPoliceOff() {
        String url = baseUrl + "/police";
        ResponseEntity<PolicePersonDTO[]> response = restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.GET,
                getVoidHttpEntity(),
                PolicePersonDTO[].class
        );
        return Arrays.asList(response.getBody());
    }

    public List<StatisticDTO> getPoliceStatistic(String policeId) {
        String url = baseUrl + "/police/statistics/" + policeId;
        ResponseEntity<StatisticDTO[]> response = restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.GET,
                getVoidHttpEntity(),
                StatisticDTO[].class
        );
        return Arrays.asList(response.getBody());
    }

    private static HttpHeaders getAuthHeaders() {
        HttpServletRequest currentRequest =
                ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                        .getRequest();

        String authHeader = currentRequest.getHeader("Authorization");

        HttpHeaders headers = new HttpHeaders();
        if (authHeader != null) {
            headers.set("Authorization", authHeader);
        }
        return headers;
    }

    private static HttpEntity<Void> getVoidHttpEntity() {
        return new HttpEntity<>(getAuthHeaders());
    }
}
