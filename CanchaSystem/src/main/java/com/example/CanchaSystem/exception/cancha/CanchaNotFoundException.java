package com.example.CanchaSystem.exception.cancha;

public class CanchaNotFoundException extends RuntimeException {
    public CanchaNotFoundException() {
        super("Cancha no encontrada");
    }
}
