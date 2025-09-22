package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.exception.owner.OwnerNotFoundException;
import com.example.CanchaSystem.model.Cancha;
import com.example.CanchaSystem.model.CanchaBrand;
import com.example.CanchaSystem.model.Owner;
import com.example.CanchaSystem.repository.OwnerRepository;
import com.example.CanchaSystem.service.CanchaBrandService;
import com.example.CanchaSystem.service.CanchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> insertCanchaBrand(@Validated @RequestBody CanchaBrand canchaBrand, Authentication auth) {

            String username = auth.getName();
            Owner owner = ownerRepository.findByUsernameAndActive(username, true)
                    .orElseThrow(() -> new OwnerNotFoundException("Dueño no encontrado"));


            canchaBrand.setOwner(owner);

            return ResponseEntity.status(HttpStatus.CREATED).body(canchaBrandService.insertCanchaBrand(canchaBrand));

    }

    @GetMapping("/findall")
    public ResponseEntity<?> getCanchaAllBrands() {
            return ResponseEntity.ok(canchaBrandService.getAllCanchaBrands());
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCanchaBrand(@RequestBody CanchaBrand canchaBrand) {
            return ResponseEntity.ok(canchaBrandService.updateCanchaBrand(canchaBrand));
    }

    @DeleteMapping("/deleteCanchaBrand/{id}")
    public ResponseEntity<?> deleteCanchaBrand(@PathVariable Long id) {
            canchaBrandService.deleteCanchaBrand(id);
            return ResponseEntity.ok(Map.of("message","Marca eliminada"));
    }

    @GetMapping("findCanchaBrand/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> findCanchaBrandById(@PathVariable Long id) {
            return ResponseEntity.ok(canchaBrandService.findCanchaBrandById(id));
    }

    @GetMapping("/findAllOwnerBrands")
    public ResponseEntity<?> findBrandsByOwnerId(Authentication auth){
        String username = auth.getName();
        return ResponseEntity.ok(canchaBrandService.findCanchaBrandsByOwnerUsername(username));
    }


    @GetMapping("/{brandId}/canchas")
    public ResponseEntity<List<Cancha>> getCanchasByBrand(@PathVariable Long brandId) {
        return ResponseEntity.ok(canchaService.getCanchasByBrandId(brandId));
    }

}
