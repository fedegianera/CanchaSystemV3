package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.dto.request.EstablishmentRequestDTO;
import com.example.CanchaSystem.service.EstablishmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/establishment")
public class EstablishmentController {
    @Autowired
    private EstablishmentService establishmentService;

    @GetMapping("/findall")
    ResponseEntity<?> getAllEstablishmentsActive() {
        return ResponseEntity.ok(establishmentService.getAllActiveEstablishment());
    }

    @PostMapping("/insert")
    ResponseEntity<?> createEstablishment(@Validated @RequestBody EstablishmentRequestDTO establishmentDto) {
        return ResponseEntity.ok(establishmentService.insertEstablishment(establishmentDto));
    }

    @DeleteMapping("/delete/{id}")
    ResponseEntity<?> deleteEstablishment(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(establishmentService.deleteEstablishment(id));
    }

    @PutMapping("/update/{id}")
    ResponseEntity<?> updateEstablishment(@PathVariable Long id, EstablishmentRequestDTO establishmentDto) {
        establishmentService.updateEstablishment(id, establishmentDto);
        return ResponseEntity.ok(Map.of("message", "Cancha actualizada correctamente"));
    }

    @GetMapping("/getEstablishmentsByBrandId/{id}")
    ResponseEntity<?> getEstablishmentsByBrandId(@PathVariable Long id) {
        return ResponseEntity.ok(establishmentService.getEstablishmentsByBrandId(id));
    }
}
