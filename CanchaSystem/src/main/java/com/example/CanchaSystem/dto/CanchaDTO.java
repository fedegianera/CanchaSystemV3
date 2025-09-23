package com.example.CanchaSystem.dto;

import com.example.CanchaSystem.model.CanchaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CanchaDTO {
    private String name;
    private String address;
    private Double totalAmount;
    private LocalTime openingHour;
    private LocalTime closingHour;
    private boolean hasRoof;
    private boolean canShower;
    private CanchaType canchaType;
    private int totalPlayers;
    private long brandId;
    private boolean working;
}
