package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.api.StreetLookupApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("map")
public class MapController {
    StreetLookupApi api = StreetLookupApi.getInstance();

    @GetMapping("/lookup/{address}")
    public ResponseEntity<?> lookupStreet(@PathVariable String address) {
        return ResponseEntity.ok(
                api.autocompleteAddress(
                        URLDecoder.decode(address, StandardCharsets.UTF_8)
                )
        );
    }

    @GetMapping("/reversegeocoding/{lat}/{lng}")
    public ResponseEntity<?> reverseGeocode(@PathVariable Double lat, @PathVariable Double lng) {
        return ResponseEntity.ok(
                api.reverseGeocodeAddress(lat, lng)
        );
    }
}
