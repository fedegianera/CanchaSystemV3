package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.dto.response.CanchaResponseDTO;
import com.example.CanchaSystem.exception.owner.OwnerNotFoundException;
import com.example.CanchaSystem.model.Cancha;
import com.example.CanchaSystem.model.Brand;
import com.example.CanchaSystem.model.Establishment;
import com.example.CanchaSystem.model.Owner;
import com.example.CanchaSystem.repository.OwnerRepository;
import com.example.CanchaSystem.service.CanchaBrandService;
import com.example.CanchaSystem.service.CanchaService;
import com.example.CanchaSystem.service.EstablishmentService;
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

    @Autowired
    private EstablishmentService establishmentService;

    @PostMapping("/insert")
    public ResponseEntity<?> insertCanchaBrand(@Validated @RequestBody Brand brand, Authentication auth) {

            String username = auth.getName();
            Owner owner = ownerRepository.findByUsernameAndActive(username, true)
                    .orElseThrow(() -> new OwnerNotFoundException("Dueño no encontrado"));


            brand.setOwner(owner);

            return ResponseEntity.status(HttpStatus.CREATED).body(canchaBrandService.insertCanchaBrand(brand));

    }

    @GetMapping("/findall")
    public ResponseEntity<?> getCanchaAllBrands() {
            return ResponseEntity.ok(canchaBrandService.getAllCanchaBrands());
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCanchaBrand(@RequestBody Brand brand) {
            return ResponseEntity.ok(canchaBrandService.updateCanchaBrand(brand));
    }

    @DeleteMapping("/deleteCanchaBrand/{id}")
    public ResponseEntity<?> deleteCanchaBrand(@PathVariable Long id) {
            canchaBrandService.deleteCanchaBrand(id);
            return ResponseEntity.ok(Map.of("message","Marca eliminada"));
    }

    @GetMapping("/findCanchaBrand/{id}")
    public ResponseEntity<?> getBrandById(@PathVariable Long id) {
        Brand brand = canchaBrandService.findCanchaBrandById(id);
        return ResponseEntity.ok(brand);

    }


    @GetMapping("/findAllOwnerBrands")
    public ResponseEntity<?> findBrandsByOwnerId(Authentication auth){
        String username = auth.getName();
        return ResponseEntity.ok(canchaBrandService.findCanchaBrandsByOwnerUsername(username));
    }

    @GetMapping("/{establishmentId}/canchas")
    public ResponseEntity<List<CanchaResponseDTO>> getCanchasByEstablishment(@PathVariable Long establishmentId) {
        return ResponseEntity.ok(canchaService.getCanchasByEstablishmentId(establishmentId));
    }

}
