package com.example.CanchaSystem.api;

import com.example.CanchaSystem.dto.AddressDTO;

import java.util.List;

public interface StreetLookupApi {
    static StreetLookupApi getInstance() {
        return GeoapifyApi.INSTANCE;
    }

    List<AddressDTO> autocompleteAddress(String text);
}
