package com.example.CanchaSystem.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationRequestDTO (
    Long establishmentId,
    Long canchaId,
    LocalDateTime reservationDate,
    LocalDateTime matchDate,
    Double deposit
){}
