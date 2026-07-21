package com.example.CanchaSystem.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddressDTO(
        @NotNull
        @Size(min = 1)
        String address,

        @NotNull
        double latitude,

        @NotNull
        double longitude
) {}
