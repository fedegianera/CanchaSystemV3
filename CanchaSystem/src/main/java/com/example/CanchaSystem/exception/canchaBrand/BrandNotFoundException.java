package com.example.CanchaSystem.exception.canchaBrand;

import com.example.CanchaSystem.exception.ResourceNotFoundException;

public class BrandNotFoundException extends ResourceNotFoundException {
    public BrandNotFoundException(Long brandId) {
        super("Marca no encontrada. id= " + brandId);
    }
}
