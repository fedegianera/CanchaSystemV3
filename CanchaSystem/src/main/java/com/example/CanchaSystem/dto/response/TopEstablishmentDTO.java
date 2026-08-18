package com.example.CanchaSystem.dto.response;

public record TopEstablishmentDTO(
        Long establishmentId,
        String establishmentName,
        long reservationCount
) {}