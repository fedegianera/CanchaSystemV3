package com.example.CanchaSystem.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public record EstablishmentRequestDTO (
        @NotNull
        @Size (min = 3, max = 15)
        String name,

        @NotNull
        Long addressId,

        @NotNull
        LocalTime openingHour,

        @NotNull
        LocalTime closingHour,

        boolean canShower,

        @NotNull
        Long brandId
) {}
