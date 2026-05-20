package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.dto.request.BrandRequestDTO;
import com.example.CanchaSystem.repository.OwnerRepository;
import com.example.CanchaSystem.service.CanchaBrandService;
import com.example.CanchaSystem.service.CanchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/canchaBrand")
public class CanchaBrandController {

    @Autowired
    private CanchaBrandService canchaBrandService;

    @Autowired
    private CanchaService canchaService;

    @Autowired
    private OwnerRepository ownerRepository;

    @PostMapping("/insert")
    public ResponseEntity<?> insertCanchaBrand(@Validated @RequestBody BrandRequestDTO brandDto, Authentication auth) {
        String username = auth.getName();

        return ResponseEntity.status(HttpStatus.CREATED).body(canchaBrandService.insertCanchaBrand(brandDto, username));
    }

    @GetMapping("/findall")
    public ResponseEntity<?> getCanchaAllBrands() {
            return ResponseEntity.ok(canchaBrandService.getAllCanchaBrands());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCanchaBrand(
            @PathVariable Long id,
            @RequestBody BrandRequestDTO brandDto) {
        return ResponseEntity.ok(canchaBrandService.updateCanchaBrand(id, brandDto));
    }


    @DeleteMapping("/deleteCanchaBrand/{id}")
    public ResponseEntity<?> deleteCanchaBrand(@PathVariable Long id) {
            canchaBrandService.deleteCanchaBrand(id);
            return ResponseEntity.ok(Map.of("message","Marca eliminada"));
    }

    @GetMapping("findCanchaBrand/{id}")
    public ResponseEntity<?> findCanchaBrandById(@PathVariable Long id) {
            return ResponseEntity.ok(canchaBrandService.findCanchaBrandById(id));
    }

    @GetMapping("/getBrandsByOwnerId/{id}")
    public ResponseEntity<?> findBrandsByOwnerId(@PathVariable UUID id){
        return ResponseEntity.ok(canchaBrandService.getBrandsByOwnerId(id));
    }


    @GetMapping("/{establishmentId}/canchas")
    public ResponseEntity<?> getCanchasByEstablishment(@PathVariable Long establishmentId) {
        return ResponseEntity.ok(canchaService.getCanchasByEstablishmentId(establishmentId));
    }

}
