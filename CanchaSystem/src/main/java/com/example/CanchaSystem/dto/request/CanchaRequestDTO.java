package com.example.CanchaSystem.dto.request;

import com.example.CanchaSystem.model.CanchaType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public record CanchaRequestDTO (
        @NotNull
        @Min(1)
        Double totalAmount,

        boolean hasRoof,
        boolean working,

        @NotNull
        CanchaType canchaType,

        @NotNull
        Long establishmentId,

        @NotNull
        boolean active
) {}
