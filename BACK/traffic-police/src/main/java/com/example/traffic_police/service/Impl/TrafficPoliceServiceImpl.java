package com.example.traffic_police.service.Impl;

import com.example.traffic_police.model.*;
import com.example.traffic_police.repository.TrafficPoliceRepo;
import com.example.traffic_police.service.TrafficPoliceService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class TrafficPoliceServiceImpl implements TrafficPoliceService {

    private final TrafficPoliceRepo repo;
    private final RestTemplate restTemplate;
    private final String mupBaseUrl;

    @Value("${mup.service.url}")
    private String mupServiceUrl;

    @Value("${spring.mail.username}")
    private String smtpEmail;

    @Value("${spring.mail.password}")
    private String smtpPassword;

    @Value("${spring.mail.host}")
    private String smtpHost;

    @Value("${spring.mail.port}")
    private int smtpPort;

    private final JavaMailSender mailSender;


    @Autowired
    public TrafficPoliceServiceImpl(
            TrafficPoliceRepo repo,
            RestTemplate restTemplate,
            @Value("${mup.service.url}") String mupBaseUrl,
            JavaMailSender mailSender
    ) {
        this.repo = repo;
        this.restTemplate = restTemplate;
        this.mupBaseUrl = mupBaseUrl;
        this.mailSender = mailSender;
    }

    // ------------------ POLICE ------------------
    @Override
    public List<PolicePerson> getAllPolice() {
        return repo.getAllPolice();
    }

    @Override
    public PolicePerson insertPolicePerson(PolicePerson police) {
        return repo.insertPolicePerson(police);
    }

    @Override
    public void suspendOfficer(String officerId) {
        repo.suspendOfficer(officerId);
    }

    @Override
    public void promoteOfficer(String officerId) {
        repo.promoteOfficer(officerId);
    }

    // ------------------ FINES ------------------
    @Override
    public List<Fine> getAllFines() {
        return repo.getAllFines();
    }

    @Override
    public void markFineAsPaid(String fineId) {
        repo.markFineAsPaid(fineId);
    }

    @Override
    public List<Fine> findUnpaidFinesByDriverId(String driverId) {
        return repo.findUnpaidFinesByDriverId(driverId);
    }

    @Override
    public Fine insertFine(Fine fine) {
        return repo.insertFine(fine);
    }

    // ------------------ VIOLATIONS ------------------
    @Override
    public List<Violation> getAllViolations() {
        return repo.getAllViolations();
    }

    @Override
    public Violation insertViolation(Violation violation) {
        return repo.insertViolation(violation);
    }

    @Override
    public void assignOfficerToViolation(String violationId, String officerId) {
        repo.assignOfficerToViolation(violationId, officerId);
    }

    @Override
    public List<Violation> getAssignedViolations(String officerId) {
        return repo.getAssignedViolations(officerId);
    }

    @Override
    public List<Violation> checkVehicleViolations(String vehicleId) {
        return repo.checkVehicleViolations(vehicleId);
    }

    @Override
    public List<Violation> getViolationHistory(String driverId) {
        return repo.getViolationHistory(driverId);
    }

    // ------------------ STATISTICS ------------------
    @Override
    public List<StatisticDTO> getDailyStatistics(String policeId) {
        return repo.getDailyStatistics(policeId);
    }

    @Override
    public byte[] exportViolationData(String format, String period) {
        return repo.exportViolationData(format, period);
    }

    @Override
    public List<OwnerDTO> fetchAllDrivers() {
        String url = mupBaseUrl + "/owners";
        ResponseEntity<OwnerDTO[]> response =
                restTemplate.getForEntity(url, OwnerDTO[].class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Failed to fetch drivers from MUP service");
        }

        return Arrays.asList(response.getBody());
    }

    @Override
    public String checkVehicleStatus(String registration) {
        String url = mupBaseUrl + "/vehicles/isStolen/" + registration;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return "Unknown response from vehicle service";
            }

            String raw = response.getBody().trim();

            return switch (raw) {
                case "True" -> "Vehicle is stolen";
                case "False" -> "Vehicle is not stolen";
                case "Vehicle does not exist" -> "Vehicle does not exist";
                default -> "Unknown response from vehicle service";
            };

        } catch (Exception e) {
            return "Error communicating with vehicle service: " + e.getMessage();
        }
    }

    @Override
    public List<OwnershipTransferDTO> getOwnershipHistoryForInvestigation(String registration) {
        String url = mupBaseUrl + "/ownerTransfers/getOwnershipTransferForVehicle/" + registration;

        try {
            ResponseEntity<OwnershipTransferDTO[]> response =
                    restTemplate.getForEntity(url, OwnershipTransferDTO[].class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Failed to fetch ownership history for " + registration);
            }

            return Arrays.asList(response.getBody());

        } catch (Exception e) {
            throw new RuntimeException("Error communicating with MUP service: " + e.getMessage(), e);
        }
    }

    @Override
    public void notifyPersonOfViolation(Violation violation, OwnerDTO owner) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(smtpEmail);
        helper.setTo(owner.getEmail());
        helper.setSubject("Traffic Violation Notice");

        String plainText = String.format(
                "Dear %s %s,\n\n" +
                        "We are notifying you about a traffic violation.\n\n" +
                        "Violation details:\n" +
                        "- Type: %s\n" +
                        "- Date: %s\n" +
                        "- Location: %s\n\n" +
                        "Please address this violation promptly.\n\n" +
                        "Best regards,\nTraffic Police Department",
                owner.getFirstName(), owner.getLastName(),
                violation.getTypeOfViolation(),
                violation.getDate(),
                violation.getLocation()
        );

        String htmlText = String.format(
                "<!DOCTYPE html><html><body>" +
                        "<h2>Traffic Violation Notice</h2>" +
                        "<p>Dear %s %s,</p>" +
                        "<p>We are notifying you about a traffic violation.</p>" +
                        "<ul>" +
                        "<li><strong>Type:</strong> %s</li>" +
                        "<li><strong>Date:</strong> %s</li>" +
                        "<li><strong>Location:</strong> %s</li>" +
                        "</ul>" +
                        "<p>Please address this violation promptly.</p>" +
                        "<p>Best regards,<br>Traffic Police Department</p>" +
                        "</body></html>",
                owner.getFirstName(), owner.getLastName(),
                violation.getTypeOfViolation(),
                violation.getDate(),
                violation.getLocation()
        );

        helper.setText(plainText, htmlText);

        mailSender.send(message);
    }

    @Override
    public List<OwnerDTO> handleDriverSuspension(SuspendDriverIdRequest dto) {
        String url = mupBaseUrl + "/driverIds/suspendDriverId";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SuspendDriverIdRequest> entity = new HttpEntity<>(dto, headers);

        restTemplate.patchForObject(url, entity, Void.class);

        return fetchAllDrivers();
    }

    @Override
    public List<OwnerDTO> handleNewViolation(NewViolationRequest dto) {
        Violation v = insertViolation(dto.getViolation());

        try {
            notifyPersonOfViolation(dto.getViolation(), dto.getOwner());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        int points;
        int amount;
        switch (dto.getViolation().getTypeOfViolation()) {
            case MINOR -> {points = 1;
                amount = 100;
            }
            case MAJOR -> {
                points = 2;
                amount = 200;
            }
            case CRITICAL -> {
                points = 3;
                amount = 400;
            }
            default -> {
                points = 0;
                amount = 0;
            }
        }
        Fine newFine = new Fine();
        newFine.setViolationID(v.getId());
        newFine.setAmount(amount);
        newFine.setDate(LocalDateTime.now());
        newFine.setPaid(false);
        insertFine(newFine);
        SuspendDriverIdRequest suspendRequest = new SuspendDriverIdRequest();
        suspendRequest.setDriverId(dto.getDriverId().getId());
        suspendRequest.setNumberOfViolationPoints(points);

        return handleDriverSuspension(suspendRequest);
    }

    @Override
    public String verifyVehicle(VehicleVerificationRequest request) {
        String url = mupServiceUrl + "/vehicles/verify";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<VehicleVerificationRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        String raw = Objects.requireNonNull(response.getBody()).trim();

        return getMessage(raw);
    }

    @Override
    public VehicleDTO reportVehicleAsStolen(String registration) {
        String url = mupServiceUrl + "/vehicles/" + registration + "/report-stolen";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<VehicleDTO> response =
                restTemplate.exchange(url, HttpMethod.POST, entity, VehicleDTO.class);

        return response.getBody();
    }

    @Override
    public List<VehicleDTO> searchVehicleByOptional(SearchVehicleRequest dto) {
        String url = mupBaseUrl + "/vehicles/search";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SearchVehicleRequest> entity = new HttpEntity<>(dto, headers);

        ResponseEntity<VehicleDTO[]> response = restTemplate.postForEntity(url, entity, VehicleDTO[].class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Failed to search vehicles: " + response.getStatusCode());
        }

        return Arrays.asList(response.getBody());
    }

    private static String getMessage(String raw) {
        String message;
        switch (raw) {
            case "Vehicle is reported as stolen" -> message = "Vehicle is reported as stolen";
            case "Vehicle does not belong to the owner, it may have been stolen" ->
                    message = "Vehicle does not belong to the owner, it may have been stolen";
            case "Vehicle does not exist" -> message = "Vehicle does not exist";
            case "All good, the vehicle belongs to the owner" -> message = "All good, the vehicle belongs to the owner";
            default -> message = "Unknown response from vehicle service";
        }
        return message;
    }

}
