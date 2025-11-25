package com.example.CanchaSystem.dto.response;

import com.example.CanchaSystem.model.CanchaType;

import java.time.LocalTime;
import java.util.List;

public record EstablishmentResponseDTO (
        Long id,
        String name,
        String address,
        LocalTime openingHour,
        LocalTime closingHour,
        boolean canShower,
        Long brandId,
        boolean active,
        double averageRating,
        List<CanchaType> types
) {}