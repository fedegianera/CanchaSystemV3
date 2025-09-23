package com.example.CanchaSystem.dto.response;

import java.util.UUID;

public record BrandResponseDTO (
     Long id,
     String brandName,
     UUID ownerId,
     boolean active
) {}
