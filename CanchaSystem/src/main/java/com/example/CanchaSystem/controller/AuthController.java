package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.dto.request.AuthRequestDTO;
import com.example.CanchaSystem.dto.request.PasswordResetRequestDTO;
import com.example.CanchaSystem.dto.request.ResetPasswordDTO;
import com.example.CanchaSystem.dto.request.VerifyResetCodeDTO;
import com.example.CanchaSystem.dto.response.AuthResponseDTO;
import com.example.CanchaSystem.model.Client;
import com.example.CanchaSystem.model.Role;
import com.example.CanchaSystem.repository.ClientRepository;
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
import com.example.CanchaSystem.service.PasswordResetService;

import java.util.Optional;
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

    @Autowired
    private ClientRepository clientRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO authRequestDTO) {
        Optional<Client> maybeClient = clientRepository.findByUsername(authRequestDTO.username());
        if (maybeClient.isPresent()) {
            Client client = maybeClient.get();
            if (!client.isVerified()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Debés verificar tu email antes de iniciar sesión");
            }
            if (!client.isActive()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tu cuenta se encuentra inactiva");
            }
        }

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

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/recovery/request")
    public ResponseEntity<?> requestPasswordReset(@RequestBody PasswordResetRequestDTO dto) {
        passwordResetService.requestReset(dto.username());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/recovery/verify")
    public ResponseEntity<?> verifyResetCode(@RequestBody VerifyResetCodeDTO dto) {
        passwordResetService.verifyCode(dto.username(), dto.code());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/recovery/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO dto) {
        passwordResetService.resetPassword(dto.username(), dto.code(), dto.newPassword());
        return ResponseEntity.ok().build();
    }
}