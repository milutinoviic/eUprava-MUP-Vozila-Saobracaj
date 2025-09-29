package com.example.traffic_police.service;



import com.example.traffic_police.model.*;
import jakarta.mail.MessagingException;


import java.util.List;

public interface TrafficPoliceService {

    // ------------------ POLICE ------------------
    List<PolicePerson> getAllPolice();
    PolicePerson insertPolicePerson(PolicePerson police);
    void suspendOfficer(String officerId);
    void promoteOfficer(String officerId);
    PolicePerson.Rank getRankOfOfficer(String officerId);

    // ------------------ FINES ------------------
    List<Fine> getAllFines();
    void markFineAsPaid(String fineId);
    List<Fine> findUnpaidFinesByDriverId(String driverId);
    Fine insertFine(Fine fine);
    Fine getFineByViolationId(String violationId);

    // ------------------ VIOLATIONS ------------------
    List<Violation> getAllViolations();
    Violation insertViolation(Violation violation);
    void assignOfficerToViolation(String violationId, String officerId);
    List<Violation> getAssignedViolations(String officerId);
    List<Violation> checkVehicleViolations(String vehicleId);
    List<Violation> getViolationHistory(String driverId);

    // ------------------ STATISTICS ------------------
    List<StatisticDTO> getDailyStatistics(String policeId);
    byte[] exportViolationData(String format, String period);
    List<OwnerDTO> fetchAllDrivers();
    String checkVehicleStatus(String registration);
    List<OwnershipTransferDTO> getOwnershipHistoryForInvestigation(String registration);

    void notifyPersonOfViolation(Violation violation, OwnerDTO owner) throws MessagingException;
    List<OwnerDTO> handleDriverSuspension(SuspendDriverIdRequest dto);
    List<OwnerDTO> handleNewViolation(NewViolationRequest dto);
    String verifyVehicle(VehicleVerificationRequest request);
    VehicleDTO reportVehicleAsStolen(String registration);
    List<VehicleDTO> searchVehicleByOptional(SearchVehicleRequest dto);
    DriverIDDTO searchDriverIDByDriverId(String driverId);
    List<VehicleDTO> findAllVehicles(String driverId);

    OwnerDTO getById(String id);

}
