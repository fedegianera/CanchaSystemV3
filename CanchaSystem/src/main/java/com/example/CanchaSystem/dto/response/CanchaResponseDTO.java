package com.example.CanchaSystem.dto.response;

import com.example.CanchaSystem.model.CanchaType;

import java.time.LocalTime;

public record CanchaResponseDTO (
        Long id,
        Double totalAmount,
        boolean hasRoof,
        boolean working,
        CanchaType canchaType,
        Long establishmentId
) {}
