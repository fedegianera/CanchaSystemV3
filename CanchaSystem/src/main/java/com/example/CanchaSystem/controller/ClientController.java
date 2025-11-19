package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.dto.request.ClientRequestDTO;
import com.example.CanchaSystem.dto.response.ClientResponseDTO;
import com.example.CanchaSystem.exception.client.ClientNotFoundException;
import com.example.CanchaSystem.model.Client;
import com.example.CanchaSystem.repository.ClientRepository;
import com.example.CanchaSystem.service.ClientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/client")

public class ClientController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getClientId(@AuthenticationPrincipal UserDetails userDetails) {
        Client client = clientRepository.findByUsernameAndActive(userDetails.getUsername(), true)
                .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado"));
        return ResponseEntity.ok(Map.of("id", client.getId()));
    }

    @GetMapping("/name")
    public ResponseEntity<?> getClientName(@AuthenticationPrincipal UserDetails userDetails) {
        Client client = clientRepository.findByUsernameAndActive(userDetails.getUsername(), true)
                .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado"));
        return ResponseEntity.ok(Map.of("name",client.getName()));
    }

    @PostMapping("/insertClient")
    public ResponseEntity<?> insertClient(@RequestBody ClientRequestDTO clientDTO) {
            return ResponseEntity.status(HttpStatus.CREATED).body(clientService.insertClient(clientDTO));
    }

    @GetMapping("/findall")
    public ResponseEntity<?> getClients() {
            return ResponseEntity.ok(clientService.getAllClients());
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateClient(@PathVariable UUID id, @RequestBody @Valid ClientRequestDTO clientDto, HttpServletRequest request) {
        clientService.updateClient(id, clientDto);

        SecurityContextHolder.clearContext();
        request.getSession().invalidate();

        return ResponseEntity.ok(Map.of("message", "Datos actualizados, inicie sesión nuevamente"));
    }

    @PutMapping("/updateAdmin")
    public ResponseEntity<?> updateClientAdmin(@RequestBody UUID id, ClientRequestDTO clientDto) {
        clientService.updateClientAdmin(id, clientDto);
        return ResponseEntity.ok("Datos actualizados");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable UUID id) {
            clientService.deleteClient(id);
            return ResponseEntity.ok(Map.of("message","Cliente eliminado"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findClientById(@PathVariable UUID id) {
            return ResponseEntity.ok(clientService.findClientById(id));
    }


    @GetMapping("/verifyUsername")
    public boolean verifyUsername(@PathVariable String username) {
        return clientService.verifyUsername(username);
    }



}
