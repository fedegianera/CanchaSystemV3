package com.example.CanchaSystem.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record ReviewRequestDTO (
        @NotNull
        Double rating,

        String message,

        @NotNull
        Long establishmentId,

        @NotNull
        UUID clientId,

        @NotNull
        String clientName,

        @NotNull
        LocalDate createdAt
) {}
