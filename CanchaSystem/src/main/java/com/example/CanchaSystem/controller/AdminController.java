package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.exception.misc.UsernameAlreadyExistsException;
import com.example.CanchaSystem.exception.admin.AdminNotFoundException;
import com.example.CanchaSystem.exception.admin.NoAdminsException;
import com.example.CanchaSystem.model.Admin;
import com.example.CanchaSystem.model.OwnerRequestStatus;
import com.example.CanchaSystem.service.AdminService;
import com.example.CanchaSystem.service.MailService;
import com.example.CanchaSystem.service.OwnerRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {


    @Autowired
    private AdminService adminService;

    @Autowired
    private OwnerRequestService ownerRequestService;

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
    public ResponseEntity<?> deleteAdmin(@PathVariable Long id) {
            adminService.deleteAdmin(id);
            return ResponseEntity.ok(Map.of("message","Administrador eliminado"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findAdminById(@PathVariable Long id) {

        return ResponseEntity.ok(adminService.findAdminById(id));

    }

    @PutMapping("/denyRequest/{requestId}")
    public ResponseEntity<?> denyRequest(@PathVariable Long requestId){
        ownerRequestService.updateRequest(requestId, OwnerRequestStatus.DENIED);
        return ResponseEntity.ok("Solicitud rechazada correctamente.");
    }

    @PutMapping("/approveRequest/{requestId}")
    public ResponseEntity<?> approveRequest(@PathVariable Long requestId){
        ownerRequestService.updateRequest(requestId, OwnerRequestStatus.APPROVED);
        return ResponseEntity.ok("Solicitud aceptada correctamente.");
    }

}
