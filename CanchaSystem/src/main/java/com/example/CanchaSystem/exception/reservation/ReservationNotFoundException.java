package com.example.CanchaSystem.exception.reservation;

public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException() {
        super("Reserva no encontrada");
    }
}
