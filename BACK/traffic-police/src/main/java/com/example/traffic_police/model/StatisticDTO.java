package com.example.traffic_police.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticDTO {
    private LocalDateTime date;
    private int numberOfViolations;
}
