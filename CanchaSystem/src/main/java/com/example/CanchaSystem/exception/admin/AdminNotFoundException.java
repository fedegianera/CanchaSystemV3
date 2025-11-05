package com.example.CanchaSystem.exception.admin;

public class AdminNotFoundException extends RuntimeException {
    public AdminNotFoundException() {
        super("Administrador no encontrado");
    }
}
