package com.example.CanchaSystem.exception;

import java.util.UUID;

public class UserVerificationNotFoundException extends ResourceNotFoundException {
    public UserVerificationNotFoundException(UUID id) {
        super("Pedido de verificación de usuario no encontrado. id= " + id);
    }
}
