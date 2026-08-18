package com.example.CanchaSystem.exception.cancha;

import com.example.CanchaSystem.exception.ResourceNotFoundException;

public class CanchaNotFoundException extends ResourceNotFoundException {
    public CanchaNotFoundException(Long canchaId) {
        super("Cancha no encontrada. id= " + canchaId);
    }

    public CanchaNotFoundException() {
        super("Cancha no encontrada");
    }
}
