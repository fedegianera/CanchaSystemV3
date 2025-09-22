package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.exception.owner.OwnerNotFoundException;
import com.example.CanchaSystem.model.Owner;
import com.example.CanchaSystem.repository.OwnerRepository;
import com.example.CanchaSystem.service.OwnerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/owner")
public class OwnerController {

    @Autowired
    OwnerService ownerService;

    @Autowired
    OwnerRepository ownerRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getClientId(@AuthenticationPrincipal UserDetails userDetails) {
        Owner owner = ownerRepository.findByUsernameAndActive(userDetails.getUsername(), true)
                .orElseThrow(() -> new OwnerNotFoundException("Dueño no encontrado"));
        return ResponseEntity.ok(owner.getId());
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertOwner(@Validated @RequestBody Owner owner) {
            return ResponseEntity.status(HttpStatus.CREATED).body(ownerService.insertOwner(owner));
    }

    @GetMapping("/findall")
    public ResponseEntity<?> getOwners() {
            return ResponseEntity.ok(ownerService.getAllOwners());
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateOwner(@RequestBody Owner owner, HttpServletRequest request) {
        ownerService.updateOwner(owner);

        SecurityContextHolder.clearContext();
        request.getSession().invalidate();

        return ResponseEntity.ok("Datos actualizados, inicie sesión nuevamente");
    }

    @PutMapping("/updateAdmin")
    public ResponseEntity<?> updateOwnerAdmin(@RequestBody Owner owner) {
        return ResponseEntity.ok(ownerService.updateOwnerAdmin(owner));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOwner(@PathVariable Long id) {
            ownerService.deleteOwner(id);
            return ResponseEntity.ok(Map.of("message","Dueño eliminado"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findOwnerById(@PathVariable Long id) {
            return ResponseEntity.ok(ownerService.findOwnerById(id));
    }

    @GetMapping("/name")
    public ResponseEntity<?> getOwnerName(@AuthenticationPrincipal UserDetails userDetails) {
        Owner owner = ownerRepository.findByUsernameAndActive(userDetails.getUsername(), true)
                .orElseThrow(() -> new OwnerNotFoundException("Dueño no encontrado"));
        return ResponseEntity.ok(Map.of(
                "name", owner.getName(),
                "bankOwner", owner.getBankOwner()
        ));
    }

    @GetMapping("/verifyUsername")
    public boolean verifyUsername(@PathVariable String username) {
        return ownerService.verifyUsername(username);
    }
}
