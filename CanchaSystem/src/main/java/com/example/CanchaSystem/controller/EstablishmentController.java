package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.dto.request.CanchaRequestDTO;
import com.example.CanchaSystem.dto.request.EstablishmentRequestDTO;
import com.example.CanchaSystem.service.EstablishmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/establishment")
public class EstablishmentController {
    @Autowired
    private EstablishmentService establishmentService;

    @GetMapping("/findAllActive")
    ResponseEntity<?> getAllEstablishmentsActive() {
        return ResponseEntity.ok(establishmentService.getAllActiveEstablishment());
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertEstablishment(@Validated @RequestBody EstablishmentRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(establishmentService.insertEstablishment(dto));
    }


}
