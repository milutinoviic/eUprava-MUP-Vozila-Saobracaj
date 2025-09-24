package com.example.traffic_police.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "violations")
public class Violation {
    @Id
    private String id;
    private TypeOfViolation typeOfViolation;
    private LocalDateTime date;
    private String location;
    private String driverId;
    private String vehicleId;
    private String policeId;

    public enum TypeOfViolation {
        MINOR, MAJOR, CRITICAL
    }

}
