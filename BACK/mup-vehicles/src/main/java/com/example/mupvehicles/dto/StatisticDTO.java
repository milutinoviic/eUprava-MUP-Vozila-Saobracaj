package com.example.mupvehicles.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StatisticDTO {
    private LocalDateTime date;
    private int numberOfViolations;
}