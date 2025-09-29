package com.example.CanchaSystem.dto.request;

import jakarta.validation.constraints.NotNull;

public record OwnerRequestDTO (
        @NotNull
        String name,

        @NotNull
        String lastName,

        @NotNull
        String username,

        @NotNull
        String mail,

        @NotNull
        String cellName
) {}
