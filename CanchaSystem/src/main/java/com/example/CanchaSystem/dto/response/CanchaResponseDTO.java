package com.example.CanchaSystem.dto.response;

import com.example.CanchaSystem.model.CanchaType;

import java.time.LocalTime;

public record CanchaResponseDTO (
        Long id,
        String name,
        String address,
        Double totalAmount,
        LocalTime openingHour,
        LocalTime closingHour,
        boolean hasRoof,
        boolean canShower,
        boolean working,
        CanchaType canchaType,
        Long brandId
) {}
