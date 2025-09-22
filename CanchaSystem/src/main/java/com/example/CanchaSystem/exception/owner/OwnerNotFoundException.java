package com.example.CanchaSystem.exception.owner;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class OwnerNotFoundException extends RuntimeException {
    public OwnerNotFoundException(String message) {
        super(message);
    }
}
