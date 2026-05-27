package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.dto.request.EstablishmentRequestDTO;
import com.example.CanchaSystem.service.EstablishmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/establishment")
public class EstablishmentController {
    @Autowired
    private EstablishmentService establishmentService;

    @GetMapping("/findall")
    ResponseEntity<?> getAllEstablishmentsActive() {
        return ResponseEntity.ok(establishmentService.getAllActiveEstablishments());
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
        establishmentService.deleteEstablishment(id);
        return ResponseEntity.ok(Map.of("message", "Establecimiento eliminado"));
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

    @GetMapping("getEstablishmentsByOwnerId/{id}")
    ResponseEntity<?> getEstablishmentsByOwnerId(@PathVariable UUID id) {
        return ResponseEntity.ok(establishmentService.getEstablishmentsByOwnerId(id));
    }

    @GetMapping("/getEstablishmentsNamesById")
    public ResponseEntity<?> getEstablishmentsNamesById(@RequestParam("ids") Long[] ids){
        System.out.println(establishmentService.getEstablishmentsNames(ids));
        return ResponseEntity.ok(establishmentService.getEstablishmentsNames(ids));
    }
}
