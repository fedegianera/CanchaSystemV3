package com.example.CanchaSystem.api;

import com.example.CanchaSystem.dto.AddressDTO;

import java.util.List;

public interface StreetLookupApi {
    List<AddressDTO> autocompleteAddress(String text);
    AddressDTO reverseGeocodeAddress(Double lat, Double lng);
}
