package com.example.CanchaSystem.dto.response;


import java.util.UUID;

public record ReviewResponseDTO (
        Long id,
        Double rating,
        String message,
        Long canchaId,
        UUID clientId
) {}
