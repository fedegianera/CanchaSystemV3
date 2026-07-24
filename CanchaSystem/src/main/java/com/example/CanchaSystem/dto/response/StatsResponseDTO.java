package com.example.CanchaSystem.dto.response;

public record StatsResponseDTO(
        long totalReservations,
        long completedReservations,
        long canceledReservations,
        long pendingReservations,
        long totalOwners,
        long totalClients
) {}