package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.exception.cancha.CanchaNotFoundException;
import com.example.CanchaSystem.exception.client.ClientNotFoundException;
import com.example.CanchaSystem.exception.client.NotEnoughMoneyException;
import com.example.CanchaSystem.exception.owner.OwnerNotFoundException;
import com.example.CanchaSystem.model.*;
import com.example.CanchaSystem.repository.CanchaRepository;
import com.example.CanchaSystem.repository.ClientRepository;
import com.example.CanchaSystem.service.ClientService;
import com.example.CanchaSystem.service.MailService;
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

    @Autowired
    private MailService mailService;

    @PostMapping("/insert")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> insertReservation(@RequestBody Reservation reservation, Authentication auth) {
        if (reservation.getMatchDate() == null || reservation.getMatchDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("La fecha del partido debe ser futura");
        }

        String username = auth.getName();
        Client client = clientRepository.findByUsernameAndActive(username, true)
                .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado"));

        reservation.setClient(client); // fuerza el cliente logueado

        Cancha cancha = canchaRepository.findById(reservation.getCancha().getId())
                .orElseThrow(() -> new CanchaNotFoundException("Cancha no encontrada"));

        reservation.setCancha(cancha);

        double deposit = cancha.getTotalAmount() / (double) cancha.getCanchaType().getTotalPlayers();

        if (client.getBankClient() < deposit)
            throw new NotEnoughMoneyException("No hay suficientes fondos");

        Owner owner = ownerService.getOwnerByCanchaId(cancha.getId())
                .orElseThrow(() -> new OwnerNotFoundException("No se encontró al dueño para la cancha"));

        clientService.payFromClientBank(client.getId(),deposit);
        ownerService.addMoneyToOwnerBank(owner.getId(),deposit);

        reservation.setReservationDate(LocalDateTime.now());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setDeposit(deposit);

        mailService.sendReservationNoticeOwner(owner.getMail(), reservation);
        mailService.sendReservationNoticeClient(client.getMail(), reservation);

        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.insertReservation(reservation));
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

    @GetMapping("/getAllMyReservations")
    @PreAuthorize("hasRole('OWNER')")
    private ResponseEntity<?> getAllMyReservationsOwner(Long ownerId){
        return ResponseEntity.ok(reservationService.getReservationsByOwnerId(ownerId));
    }

    @GetMapping("/getAllMyReservationsByBrand")
    @PreAuthorize("hasRole('OWNER')")
    private ResponseEntity<?> getAllMyReservationsByBrand(Long brandId){
        return ResponseEntity.ok(reservationService.getReservationsByBrandId(brandId));
    }

    @GetMapping("/getReservationsByCanchaId/{canchaId}")
    public ResponseEntity<?> getReservationsByCanchaId(@PathVariable Long canchaId) {
        return ResponseEntity.ok(reservationService.findReservationsByCanchaId(canchaId));
     }
}
