package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.dto.request.AuthRequestDTO;
import com.example.CanchaSystem.dto.response.AuthResponseDTO;
import com.example.CanchaSystem.model.Role;
import com.example.CanchaSystem.service.JWTService;
import com.example.CanchaSystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO authRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequestDTO.username(),
                        authRequestDTO.password()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        String roleString = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "User has no roles assigned"));

        Role role = Role.getRole(roleString)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Unknown role: " + roleString));

        UUID id = userService.getUserIdByUsername(userDetails.getUsername(), role)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return ResponseEntity.ok(new AuthResponseDTO(
                token,
                roleString,
                userDetails.getUsername(),
                id
        ));
    }
}
