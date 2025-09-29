package com.example.CanchaSystem.dto.response;

public record AuthResponseDTO (
        String token,
        String role,
        String username
)
{}
