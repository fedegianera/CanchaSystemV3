package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.model.Admin;
import com.example.CanchaSystem.service.AdminService;
import com.example.CanchaSystem.service.ClientService;
import com.example.CanchaSystem.service.StatsAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private ClientService clientService;

    @PostMapping("/insert")
    public ResponseEntity<?> insertAdmin(@Validated @RequestBody Admin admin) {
            return ResponseEntity.status(HttpStatus.CREATED).body(adminService.insertAdmin(admin));
    }

    @GetMapping("/findall")
    public ResponseEntity<?> getAdmins() {
            return ResponseEntity.ok(adminService.getAllAdmins());
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

    @GetMapping("/promote/{id}")
    public ResponseEntity<?> promoteClient(@PathVariable UUID id) {
        return ResponseEntity.ok(clientService.turnClientToOwner(id));
    }

    @Autowired
    private StatsAdminService statsAdminService;

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime until) {
        if (from != null && until != null) {
            return ResponseEntity.ok(statsAdminService.getStatsByPeriod(from, until));
        }
        return ResponseEntity.ok(statsAdminService.getGeneralStats());
    }
}
