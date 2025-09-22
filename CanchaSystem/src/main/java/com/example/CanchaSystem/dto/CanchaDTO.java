package com.example.CanchaSystem.dto;

import com.example.CanchaSystem.model.CanchaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CanchaDTO {
    private String name;
    private String address;
    private Double totalAmount;
    private String openingHour;
    private String closingHour;
    private boolean hasRoof;
    private boolean canShower;
    private CanchaType canchaType;
    private int totalPlayers;
    private BrandDTO brand;
    private boolean working;
}
