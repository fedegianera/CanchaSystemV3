package com.example.CanchaSystem.exception.review;

import com.example.CanchaSystem.exception.ResourceNotFoundException;

public class ReviewNotFoundException extends ResourceNotFoundException {
    public ReviewNotFoundException(Long reviewId) {
        super("Reseña no encontrada. id= " + reviewId);
    }
}
