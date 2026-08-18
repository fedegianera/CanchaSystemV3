package com.example.CanchaSystem.dto.response;

import com.example.CanchaSystem.model.CanchaType;
import com.example.CanchaSystem.model.ReservationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationResponseDTO (
    Long id,
    UUID clientId,
    Long establishmentId,
    Long canchaId,
    CanchaType canchaType,
    LocalDateTime reservationDate,
    LocalDateTime matchDate,
    ReservationStatus status
){}
