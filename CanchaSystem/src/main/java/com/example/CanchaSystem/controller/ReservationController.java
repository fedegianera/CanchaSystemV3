package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.dto.request.ReservationRequestDTO;
import com.example.CanchaSystem.exception.cancha.CanchaNotFoundException;
import com.example.CanchaSystem.exception.client.ClientNotFoundException;
import com.example.CanchaSystem.exception.client.NotEnoughMoneyException;
import com.example.CanchaSystem.exception.owner.OwnerNotFoundException;
import com.example.CanchaSystem.model.*;
import com.example.CanchaSystem.repository.CanchaRepository;
import com.example.CanchaSystem.repository.ClientRepository;
import com.example.CanchaSystem.service.ClientService;
import com.example.CanchaSystem.service.OwnerService;
import com.example.CanchaSystem.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private CanchaRepository canchaRepository;

    @PostMapping("/insert")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> insertReservation(@RequestBody ReservationRequestDTO reservationDTO, Authentication auth) {
        if (reservationDTO.matchDate() == null || reservationDTO.matchDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("La fecha del partido debe ser futura");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.insertReservation(reservationDTO, auth));
    }

    @GetMapping("/findall")
    public ResponseEntity<?> getReservations() {
            return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateReservation(@RequestBody Reservation reservation) {
            return ResponseEntity.ok(reservationService.updateReservation(reservation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable Long id) {
            reservationService.deleteReservation(id);
            return ResponseEntity.ok(Map.of("message","Reserva eliminada"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findReservationById(@PathVariable Long id) {
            return ResponseEntity.ok(reservationService.findReservationById(id));
    }

    @GetMapping("/findReservationsByClient")
    public ResponseEntity<?> findReservationsByClient(Authentication auth){
        String username = auth.getName();

        return ResponseEntity.ok(reservationService.findReservationsByClient(username));
    }

    @GetMapping("/getAvailableHours/{canchaId}/{day}")
    public ResponseEntity<List<LocalTime>> obtainAvailableHours(
            @PathVariable Long canchaId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day) {
            List<LocalTime> hours = reservationService.getAvailableHours(canchaId, day);
            if (hours == null || hours.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(hours);
    }


    @GetMapping("/getReservationsByCanchaId/{canchaId}")
    public ResponseEntity<?> getReservationsByCanchaId(@PathVariable Long canchaId) {
        return ResponseEntity.ok(reservationService.findReservationsByCanchaId(canchaId));
     }
}
