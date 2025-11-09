package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.dto.request.BrandRequestDTO;
import com.example.CanchaSystem.dto.request.EstablishmentRequestDTO;
import com.example.CanchaSystem.model.CanchaType;
import com.example.CanchaSystem.service.CanchaService;
import com.example.CanchaSystem.service.EstablishmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/establishment")
public class EstablishmentController {
    @Autowired
    private EstablishmentService establishmentService;

    @Autowired
    private CanchaService canchaService;

    @GetMapping("/findall")
    ResponseEntity<?> getAllEstablishmentsActive() {
        return ResponseEntity.ok(establishmentService.getAllActiveEstablishment());
    }

    @GetMapping("/find/{id}")
    ResponseEntity<?> getActiveEstablishmentById(@PathVariable Long id) {
        return ResponseEntity.ok(establishmentService.getEstablishment(id));
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
    public ResponseEntity<?> updateEstablishment(
            @PathVariable Long id,
            @RequestBody EstablishmentRequestDTO estDto) {
        return ResponseEntity.ok(establishmentService.updateEstablishment(id, estDto));
    }

    @GetMapping("/getEstablishmentsByBrandId/{id}")
    ResponseEntity<?> getEstablishmentsByBrandId(@PathVariable Long id) {
        return ResponseEntity.ok(establishmentService.getEstablishmentsByBrandId(id));
    }

    @GetMapping("/getCanchaTypes/{id}")
    public ResponseEntity<List<CanchaType>> getCanchaTypes(@PathVariable("id") Long establishmentId) {
        List<CanchaType> types = canchaService.getCanchaTypesByEstablishment(establishmentId);
        if (types == null || types.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(types);
    }
}
