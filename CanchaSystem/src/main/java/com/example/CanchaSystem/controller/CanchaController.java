package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.exception.cancha.CanchaNameAlreadyExistsException;
import com.example.CanchaSystem.exception.cancha.CanchaNotFoundException;
import com.example.CanchaSystem.exception.cancha.IllegalCanchaAddressException;
import com.example.CanchaSystem.exception.cancha.NoCanchasException;
import com.example.CanchaSystem.model.Cancha;
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
import java.util.Optional;

@RestController
@RequestMapping("/cancha")
public class CanchaController {

    @Autowired
    private CanchaService canchaService;

    @PostMapping("/insert")
    public ResponseEntity<?> insertCancha(@Validated @RequestBody Cancha cancha) {
            return ResponseEntity.status(HttpStatus.CREATED).body(canchaService.insertCancha(cancha));
    }

    @GetMapping("/findall")
    public ResponseEntity<?> getAllCanchas() {
            return ResponseEntity.ok(canchaService.getAllCanchas());
    }

    @GetMapping("/findallactive")
    public ResponseEntity<?> getAllCanchasActive() {
        return ResponseEntity.ok(canchaService.getAllActiveCanchas());
    }

    @GetMapping("/findMyCanchas")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> getAllMyCanchas(Authentication auth) {
        String username = auth.getName();
        return ResponseEntity.ok(canchaService.getCanchasByOwner(username));
    }

    @PutMapping("/updateAny")
    public ResponseEntity<?> updateAnyCancha(@RequestBody Cancha cancha) {
            canchaService.updateCancha(cancha.getId(),cancha);
            return ResponseEntity.ok(Map.of("message", "Cancha actualizada correctamente"));
    }

    @PutMapping("/updateMyCancha")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> updateMyCancha(@RequestBody Cancha cancha, Authentication auth) {
        String username = auth.getName();
        canchaService.updateOwnerCancha(cancha,username);
        return ResponseEntity.ok(Map.of("message", "Cancha actualizada correctamente"));
    }

    @DeleteMapping("/dropCanchaById/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAnyCancha(@PathVariable Long id) {
            canchaService.deleteCancha(id);
            return ResponseEntity.ok(Map.of("message", "Cancha eliminada correctamente"));
    }

    @DeleteMapping("/dropMyCanchaById/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> deleteMyCancha(@PathVariable Long id, Authentication auth) {
        String username = auth.getName();
        canchaService.deleteOwnerCancha(id, username);
        return ResponseEntity.ok(Map.of("message", "Cancha eliminada correctamente"));
    }

    @GetMapping("/findCanchaById/{id}")
    public ResponseEntity<?> findAnyCanchaById(@PathVariable Long id) {
            Cancha cancha = canchaService.findCanchaById(id);
            return ResponseEntity.ok(cancha);
    }

    @GetMapping("/getCanchasByOwnerId/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getCanchasByOwnerId(@PathVariable Long id) {
            return ResponseEntity.ok(canchaService.getCanchasByOwnerId(id));
    }

    @GetMapping("/getCanchasByBrandId/{id}")
    public ResponseEntity<?> getCanchasByBrandId(@PathVariable Long id) {
            return ResponseEntity.ok(canchaService.getCanchasByBrandId(id));
    }
}
