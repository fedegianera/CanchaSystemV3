package com.example.CanchaSystem.dto.response;

import java.util.List;

public record StatsResponseDTO(
        long totalReservations,
        long completedReservations,
        long canceledReservations,
        long pendingReservations,
        long totalOwners,
        long totalClients,
        long totalActiveCanchas,
        long totalActiveEstablishments,
        long totalActiveBrands,
        double averageReservationsPerDay,
        double cancellationRate,
        List<CanchaTypeCountDTO> canchasByType,
        List<TopEstablishmentDTO> topEstablishments
) {}