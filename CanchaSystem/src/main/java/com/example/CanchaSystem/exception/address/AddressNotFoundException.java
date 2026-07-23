package com.example.CanchaSystem.exception.address;

import com.example.CanchaSystem.exception.ResourceNotFoundException;

public class AddressNotFoundException extends ResourceNotFoundException {
    public AddressNotFoundException(Long addressId) {
        super("Dirección no encontrada. id=" + addressId);
    }

    public AddressNotFoundException() {
        super("Dirección no encontrada");
    }
}
