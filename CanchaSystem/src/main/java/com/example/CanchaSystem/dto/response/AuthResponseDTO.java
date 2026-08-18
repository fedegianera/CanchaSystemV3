package com.example.CanchaSystem.dto.response;

import java.util.UUID;

public record AuthResponseDTO (
        String token,
        String role,
        String username,
        UUID id
)
{}
