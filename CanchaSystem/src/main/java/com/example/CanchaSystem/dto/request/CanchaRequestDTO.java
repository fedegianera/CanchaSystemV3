package com.example.CanchaSystem.dto.request;

import com.example.CanchaSystem.model.CanchaType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public record CanchaRequestDTO (
        @NotNull
        @Size (min = 3, max = 15)
        String name,

        @NotNull
        String address,

        @NotNull
        @Min(1)
        Double totalAmount,

        @NotNull
        LocalTime openingHour,

        @NotNull
        LocalTime closingHour,

        boolean hasRoof,
        boolean canShower,
        boolean working,

        @NotNull
        CanchaType canchaType,

        @NotNull
        Long brandId
) {}
