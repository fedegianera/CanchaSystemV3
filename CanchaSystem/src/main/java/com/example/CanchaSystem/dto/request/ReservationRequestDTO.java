package com.example.CanchaSystem.dto.request;

import com.example.CanchaSystem.model.ReservationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationRequestDTO (
    Long establishmentId,
    Long canchaId,
    LocalDateTime reservationDate,
    ReservationStatus status,
    LocalDateTime matchDate
){}
