package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.dto.request.CanchaRequestDTO;
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
import java.util.UUID;

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
    public ResponseEntity<?> insertReservation(@RequestBody ReservationRequestDTO reservationDTO, Authentication auth) {
        if (reservationDTO.matchDate() == null || reservationDTO.matchDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of("error", "La fecha del partido debe ser futura"));
        }

        Reservation reservation = reservationService.insertReservation(reservationDTO, auth);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Reserva hecha",
                        "reservationId", reservation.getId(),
                        "matchDate", reservation.getMatchDate(),
                        "status", reservation.getStatus().name()
                ));
    }


    @GetMapping("/findall")
    public ResponseEntity<?> getReservations() {
            return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReservation(
            @PathVariable Long id,
            @RequestBody ReservationRequestDTO reservationRequestDTO) {
        return ResponseEntity.ok(reservationService.updateReservation(id, reservationRequestDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findReservationById(@PathVariable Long id) {
            return ResponseEntity.ok(reservationService.findReservationById(id));
    }

    @GetMapping("/findReservationsByClientId/{id}")
    public ResponseEntity<?> findReservationsByClientId(@PathVariable("id") UUID id){
        return ResponseEntity.ok(reservationService.findReservationsByClientId(id));
    }

    @GetMapping("/getAvailableHours/{establishmentId}/{day}")
    public ResponseEntity<?> obtainAvailableHours(
            @PathVariable Long establishmentId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day) {
            Map<String, List<LocalTime>> hours = reservationService.getAvailableHours(establishmentId, day);
            if (hours == null || hours.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(hours);
    }


    @GetMapping("/getReservationsByCanchaId/{establishmentId}")
    public ResponseEntity<?> getReservationsByCanchaId(@PathVariable Long canchaId) {
        return ResponseEntity.ok(reservationService.findReservationsByCanchaId(canchaId));
     }


    @DeleteMapping("/cancelReservation/{id}")
    public ResponseEntity<?> cancelReservationById(@PathVariable("id") Long id){
        return ResponseEntity.ok(reservationService.cancelReservation(id));
    }

}
