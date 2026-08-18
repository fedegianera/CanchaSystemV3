package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.api.GeoapifyApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("map")
public class MapController {
    @Autowired
    private GeoapifyApi streetLookupApi;

    @GetMapping("/autocomplete/{address}")
    public ResponseEntity<?> lookupStreet(@PathVariable String address) {
        try {
            return ResponseEntity.ok(
                    streetLookupApi.autocompleteAddress(
                            URLEncoder.encode(address, StandardCharsets.UTF_8)
                    )
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/reversegeocoding/{lat}/{lng}")
    public ResponseEntity<?> reverseGeocode(@PathVariable Double lat, @PathVariable Double lng) {
        return ResponseEntity.ok(
                streetLookupApi.reverseGeocodeAddress(lat, lng)
        );
    }
}
