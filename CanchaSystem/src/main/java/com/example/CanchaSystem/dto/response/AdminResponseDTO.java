package com.example.CanchaSystem.dto.response;
import java.util.UUID;

public record AdminResponseDTO (
    UUID adminId,
    String username,
    String role
) {}
