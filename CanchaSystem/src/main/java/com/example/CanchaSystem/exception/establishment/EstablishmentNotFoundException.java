package com.example.CanchaSystem.exception.establishment;

import com.example.CanchaSystem.exception.ResourceNotFoundException;

public class EstablishmentNotFoundException extends ResourceNotFoundException {
    public EstablishmentNotFoundException(Long establishmentId) {
        super("Establecimiento no encontrado. id= " + establishmentId);
    }
}
