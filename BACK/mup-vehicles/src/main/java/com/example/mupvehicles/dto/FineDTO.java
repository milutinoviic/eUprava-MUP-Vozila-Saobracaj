package com.example.mupvehicles.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FineDTO {
    private String id;
    private double amount;
    private boolean isPaid;
    private LocalDateTime date;
    private String violationID;
}