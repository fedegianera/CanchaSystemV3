package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.service.UserVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/verify")
public class UserVerificationController {
    @Autowired
    private UserVerificationService userVerificationService;

    @GetMapping("/{token}")
    private ResponseEntity<?> verifyClient(@PathVariable String token) {
        userVerificationService.verifyClient(token);
        return ResponseEntity.ok(Map.of("message","Usuario autorizado exitosamente"));
    }
}
