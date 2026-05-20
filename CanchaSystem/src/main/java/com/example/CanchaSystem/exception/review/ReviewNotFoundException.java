package com.example.CanchaSystem.exception.review;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(Long reviewId) {
        super("Reseña no encontrada. id= " + reviewId);
    }
}
