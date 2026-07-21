package com.example.CanchaSystem.dto.request;

import com.example.CanchaSystem.dto.AddressDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public record EstablishmentRequestDTO (
        @NotNull
        @Size (min = 3, max = 15)
        String name,

        @NotNull
        AddressDTO address,

        @NotNull
        LocalTime openingHour,

        @NotNull
        LocalTime closingHour,

        boolean canShower,

        @NotNull
        Long brandId
) {}
