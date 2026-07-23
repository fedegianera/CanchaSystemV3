package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.api.StreetLookupApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("map")
public class MapController {
    @PostMapping("/lookup/{address}")
    public ResponseEntity<?> lookupStreet(@PathVariable String address) {
        return ResponseEntity.ok(
                StreetLookupApi.getInstance().autocompleteAddress(address)
        );
    }
}
