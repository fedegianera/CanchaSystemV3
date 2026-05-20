package com.example.CanchaSystem.exception.reservation;

import com.example.CanchaSystem.exception.ResourceNotFoundException;

public class ReservationNotFoundException extends ResourceNotFoundException {
    public ReservationNotFoundException(Long reservationId) {
        super("Reserva no encontrada. id= " + reservationId);
    }
}
