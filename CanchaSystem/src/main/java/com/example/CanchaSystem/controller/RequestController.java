package com.example.CanchaSystem.controller;


import com.example.CanchaSystem.service.OwnerRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/request")
@PreAuthorize("hasRole('ADMIN')")
public class RequestController {

    @Autowired
    private OwnerRequestService ownerRequestService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllRequests(){
        return ResponseEntity.ok(ownerRequestService.getAllRequests());
    }

    @GetMapping("/getAllPending")
    public ResponseEntity<?> getAllPendingRequests(){
        return ResponseEntity.ok(ownerRequestService.getAllPendingRequests());
    }
}
