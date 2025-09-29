package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.model.Admin;
import com.example.CanchaSystem.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {


    @Autowired
    private AdminService adminService;

    @PostMapping("/insert")
    public ResponseEntity<?> insertAdmin(@Validated @RequestBody Admin admin) {
            return ResponseEntity.status(HttpStatus.CREATED).body(adminService.insertAdmin(admin));
    }

    @GetMapping("/findall")
    public ResponseEntity<?> getAdmins() {
            return ResponseEntity.ok(adminService.getAllAdmins());
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateAdmin(@RequestBody Admin admin) {
            return ResponseEntity.ok(adminService.updateAdmin(admin));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable UUID id) {
            adminService.deleteAdmin(id);
            return ResponseEntity.ok(Map.of("message","Administrador eliminado"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findAdminById(@PathVariable UUID id) {

        return ResponseEntity.ok(adminService.findAdminById(id));

    }

}
