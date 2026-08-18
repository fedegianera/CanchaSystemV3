package com.example.CanchaSystem.exception.image;

import com.example.CanchaSystem.exception.ResourceNotFoundException;

public class ImageNotFoundException extends ResourceNotFoundException {
    public ImageNotFoundException(Long imageId) {
        super("Imagen no encontrada. id= " + imageId);
    }

    public ImageNotFoundException() {
        super("Imagen no encontrada");
    }

    public ImageNotFoundException(String path) {
        super("Imagen no encontrada. path= " + path);
    }
}
