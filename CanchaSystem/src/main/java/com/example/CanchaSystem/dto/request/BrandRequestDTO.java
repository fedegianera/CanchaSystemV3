package com.example.CanchaSystem.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record BrandRequestDTO (
    @NotNull
    @Size(min = 3, max = 15)
    String brandName,

    @NotNull
    UUID ownerId
) {}
