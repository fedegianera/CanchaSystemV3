package com.example.CanchaSystem.dto.response;

import java.util.UUID;

public record ClientResponseDTO (
        UUID id,
        String name,
        String lastName,
        String username,
        String mail,
        String cellNumber,
        String role
) {}
