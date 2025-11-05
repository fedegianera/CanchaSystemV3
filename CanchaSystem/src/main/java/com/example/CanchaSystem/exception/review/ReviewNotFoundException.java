package com.example.CanchaSystem.exception.review;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException() {
        super("Reseña no encontrada");
    }
}
