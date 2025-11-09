package com.example.CanchaSystem.dto.response;


import java.time.LocalDate;
import java.util.UUID;

public record ReviewResponseDTO (
        Long id,
        Double rating,
        String message,
        Long establishmentId,
        UUID clientId,
        String clientName,
        LocalDate createdAt
) {}
