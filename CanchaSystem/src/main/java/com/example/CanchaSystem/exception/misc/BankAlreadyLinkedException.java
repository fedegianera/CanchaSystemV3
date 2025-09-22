package com.example.CanchaSystem.exception.misc;

public class BankAlreadyLinkedException extends RuntimeException {
    public BankAlreadyLinkedException(String message) {
        super(message);
    }
}
