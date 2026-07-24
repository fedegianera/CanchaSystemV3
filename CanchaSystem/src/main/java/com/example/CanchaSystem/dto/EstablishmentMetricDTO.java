package com.example.CanchaSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EstablishmentMetricDTO {
    private Long establishmentId;
    private String name;
    private double value;
}
