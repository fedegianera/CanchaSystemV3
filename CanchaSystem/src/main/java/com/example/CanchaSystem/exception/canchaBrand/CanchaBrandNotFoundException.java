package com.example.CanchaSystem.exception.canchaBrand;

public class CanchaBrandNotFoundException extends RuntimeException {
    public CanchaBrandNotFoundException() {
        super("Marca no encontrada");
    }
}
