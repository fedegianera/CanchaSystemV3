package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.exception.client.ClientNotFoundException;
import com.example.CanchaSystem.model.Client;
import com.example.CanchaSystem.model.OwnerRequest;
import com.example.CanchaSystem.repository.ClientRepository;
import com.example.CanchaSystem.service.ClientService;
import com.example.CanchaSystem.service.OwnerRequestService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/client")

public class ClientController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OwnerRequestService ownerRequestService;

    @GetMapping("/me")
    public ResponseEntity<?> getClientId(@AuthenticationPrincipal UserDetails userDetails) {
        Client client = clientRepository.findByUsernameAndActive(userDetails.getUsername(), true)
                .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado"));
        return ResponseEntity.ok(Map.of("id",client.getId()));
    }

    @GetMapping("/name")
    public ResponseEntity<?> getClientName(@AuthenticationPrincipal UserDetails userDetails) {
        Client client = clientRepository.findByUsernameAndActive(userDetails.getUsername(), true)
                .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado"));
        return ResponseEntity.ok(Map.of("name",client.getName()));
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertClient(@Validated @RequestBody Client client) {
            return ResponseEntity.status(HttpStatus.CREATED).body(clientService.insertClient(client));
    }

    @GetMapping("/findall")
    public ResponseEntity<?> getClients() {
            return ResponseEntity.ok(clientService.getAllClients());
    }


    @PutMapping("/update")
    public ResponseEntity<?> updateClient(@RequestBody Client client, HttpServletRequest request) {
        clientService.updateClient(client);

        SecurityContextHolder.clearContext();
        request.getSession().invalidate();

        return ResponseEntity.ok("Datos actualizados, inicie sesión nuevamente");
    }

    @PutMapping("/addMoneyToClient/{clientId}/{amount}")
    public ResponseEntity<?> AddMoneyToBankClient(@PathVariable Long clientId,@PathVariable double amount){

        clientService.addMoneyToClientBank(clientId,amount);

        return ResponseEntity.ok("Saldo actualizado");
    }

    @PutMapping("/updateAdmin")
    public ResponseEntity<?> updateClientAdmin(@RequestBody Client client) {
        clientService.updateClientAdmin(client);
        return ResponseEntity.ok("Datos actualizados");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable Long id) {
            clientService.deleteClient(id);
            return ResponseEntity.ok(Map.of("message","Cliente eliminado"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findClientById(@PathVariable Long id) {
            return ResponseEntity.ok(clientService.findClientById(id));
    }

    @PostMapping("/request")
    public ResponseEntity<?> request(@Validated @RequestBody OwnerRequest ownerRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(ownerRequestService.insertRequest(ownerRequest));
    }

    @GetMapping("/verifyUsername")
    public boolean verifyUsername(@PathVariable String username) {
        return clientService.verifyUsername(username);
    }

    @GetMapping("/request/{clientId}")
    public ResponseEntity<OwnerRequest> getRequestByClientId(@PathVariable Long clientId) {
        OwnerRequest request = ownerRequestService.findByClientId(clientId);
        return ResponseEntity.ok(request);
    }


}
