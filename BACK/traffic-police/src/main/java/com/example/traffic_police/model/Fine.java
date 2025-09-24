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
@Document(collection = "fines")
public class Fine {

    @Id
    private String id;            // MongoDB _id
    private double amount;
    private boolean isPaid;
    private LocalDateTime date;
    private String violationID;
}
