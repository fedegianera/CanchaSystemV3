package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.dto.request.AuthRequestDTO;
import com.example.CanchaSystem.dto.response.AuthResponseDTO;
import com.example.CanchaSystem.model.Admin;
import com.example.CanchaSystem.model.Client;
import com.example.CanchaSystem.model.Owner;
import com.example.CanchaSystem.repository.AdminRepository;
import com.example.CanchaSystem.repository.ClientRepository;
import com.example.CanchaSystem.repository.OwnerRepository;
import com.example.CanchaSystem.service.JWTService;
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
    private ClientRepository clientRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private AdminRepository adminRepository;

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

        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "User has no roles assigned"));

        UUID id;

        // id dependiendo del rol
        switch (role) {
            case "ROLE_OWNER":
                id = ownerRepository.findByUsername(userDetails.getUsername())
                        .map(Owner::getId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found"));
                break;

            case "ROLE_CLIENT":
                id = clientRepository.findByUsername(userDetails.getUsername())
                        .map(Client::getId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));
                break;
            case "ROLE_ADMIN":
                id = adminRepository.findByUsername(userDetails.getUsername())
                        .map(Admin::getId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));

                break;
            default:
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unknown role: " + role);
        }

        return ResponseEntity.ok(new AuthResponseDTO(
                token,
                role,
                userDetails.getUsername(),
                id
        ));
    }
}
