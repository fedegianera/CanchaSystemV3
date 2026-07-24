package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.dto.AddressDTO;
import com.example.CanchaSystem.service.AddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("address")
public class AddressController {
    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertAddress(@RequestBody AddressDTO addressDTO) {
        return ResponseEntity.ok(addressService.insertAddress(addressDTO));
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> getAddressById(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.findAddressByIdOrThrow(id));
    }
}
