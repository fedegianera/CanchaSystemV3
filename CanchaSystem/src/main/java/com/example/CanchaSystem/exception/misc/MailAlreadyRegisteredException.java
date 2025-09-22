package com.example.CanchaSystem.exception.misc;

public class MailAlreadyRegisteredException extends RuntimeException {
    public MailAlreadyRegisteredException(String message) {
        super(message);
    }
}
