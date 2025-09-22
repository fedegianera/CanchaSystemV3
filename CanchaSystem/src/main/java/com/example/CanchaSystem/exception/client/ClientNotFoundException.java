package com.example.CanchaSystem.exception.client;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(String message) {
        super(message);
    }
}
