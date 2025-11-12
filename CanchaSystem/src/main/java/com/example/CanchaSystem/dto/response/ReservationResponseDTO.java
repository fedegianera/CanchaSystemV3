package com.example.CanchaSystem.dto.response;

import com.example.CanchaSystem.model.ReservationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationResponseDTO (
    Long id,
    UUID clientId,
    //Long establishmentId,
    Long canchaId,
    LocalDateTime reservationDate,
    LocalDateTime matchDate,
    ReservationStatus status
){}
