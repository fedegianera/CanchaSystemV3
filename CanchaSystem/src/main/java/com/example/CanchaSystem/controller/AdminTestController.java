package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.model.Admin;
import com.example.CanchaSystem.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminTestController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/testInsert")
    public ResponseEntity<?> insertAdmin(@RequestBody Admin admin) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.insertAdmin(admin));
    }
}
