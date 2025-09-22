package com.example.CanchaSystem.exception.client;

public class ClientAlreadyRequestedException extends RuntimeException {
    public ClientAlreadyRequestedException(String message) {
        super(message);
    }
}
