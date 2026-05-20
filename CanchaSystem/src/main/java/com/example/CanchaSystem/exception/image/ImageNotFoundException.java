package com.example.CanchaSystem.exception.image;

import com.example.CanchaSystem.exception.ResourceNotFoundException;

public class ImageNotFoundException extends ResourceNotFoundException {
    public ImageNotFoundException(Long imageId) {
        super("Imagen no encontrada. id= " + imageId);
    }
}
