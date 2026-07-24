package com.example.CanchaSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PeakHourDTO {
    private int hour;
    private long reservationCount;
}
