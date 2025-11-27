package com.example.CanchaSystem.service;


import com.example.CanchaSystem.Mapper.CanchaMapper;
import com.example.CanchaSystem.Mapper.EstablishmentMapper;
import com.example.CanchaSystem.Mapper.ReservationMapper;
import com.example.CanchaSystem.dto.request.ReservationRequestDTO;
import com.example.CanchaSystem.dto.response.ReservationResponseDTO;
import com.example.CanchaSystem.exception.cancha.CanchaNotFoundException;
import com.example.CanchaSystem.exception.client.ClientNotFoundException;
import com.example.CanchaSystem.exception.reservation.IllegalReservationDateException;
import com.example.CanchaSystem.exception.reservation.NoReservationsException;
import com.example.CanchaSystem.exception.reservation.ReservationNotFoundException;
import com.example.CanchaSystem.model.*;
import com.example.CanchaSystem.repository.CanchaRepository;
import com.example.CanchaSystem.repository.ClientRepository;
import com.example.CanchaSystem.repository.EstablishmentRepository;
import com.example.CanchaSystem.repository.ReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CanchaRepository canchaRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private EstablishmentRepository establishmentRepository;

    @Autowired
    private EstablishmentMapper establishmentMapper;

    @Autowired
    private CanchaMapper canchaMapper;

    @Autowired
    private ReservationMapper reservationMapper;


    public ReservationResponseDTO insertReservation(ReservationRequestDTO reservationDTO, Authentication auth)
            throws IllegalReservationDateException {

        String username = auth.getName();
        Client client = clientRepository.findByUsernameAndActive(username, true)
                .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado"));

        LocalDateTime correctedMatchDate = reservationDTO.matchDate()
                .atOffset(ZoneOffset.UTC)
                .withOffsetSameInstant(ZoneOffset.of("-03:00"))
                .toLocalDateTime();

        List<Cancha> canchas = canchaRepository.findByEstablishmentIdAndCanchaType(
                reservationDTO.establishmentId(),
                reservationDTO.canchaType()
        );

        if (canchas.isEmpty()) {
            throw new CanchaNotFoundException("No existen canchas de ese tipo en este establecimiento");
        }

        Cancha canchaDisponible = canchas.stream()
                .filter(c -> !reservationRepository.existsByMatchDateAndCanchaIdAndStatus(
                        correctedMatchDate,
                        c.getId(),
                        ReservationStatus.PENDING
                ))
                .findFirst()
                .orElseThrow(() -> new IllegalReservationDateException(
                        "No hay disponibilidad para ese tipo de cancha en ese horario"
                ));

        Reservation reservation = Reservation.builder()
                .client(client)
                .cancha(canchaDisponible)
                .reservationDate(reservationDTO.reservationDate())
                .matchDate(correctedMatchDate)
                .status(ReservationStatus.PENDING)
                .build();

        reservationRepository.save(reservation);

        return reservationMapper.toDto(reservation);
    }



    public List<ReservationResponseDTO> getAllReservations() throws NoReservationsException {
        List<Reservation> reservations = reservationRepository.findAll();

        if (reservations.isEmpty()) {
            throw new NoReservationsException("Aun no hay reservas hechas");
        }


        return reservationMapper.toDto(reservations);
    }

    public ReservationResponseDTO updateReservation(Long id, ReservationRequestDTO reservationRequestDTO) throws ReservationNotFoundException {
        Optional<Reservation> reservationOpt = reservationRepository.findById(id);

        if (reservationOpt.isEmpty()) {
            throw new ReservationNotFoundException("Reserva no encontrada");
        }

        Reservation reservation = reservationOpt.get();

        reservation.setStatus(reservationRequestDTO.status());
        reservation.setMatchDate(reservationRequestDTO.matchDate());

        reservationRepository.save(reservation);

        return reservationMapper.toDto(reservation);
    }

    public ReservationResponseDTO findReservationById(Long id) throws ReservationNotFoundException {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(()-> new ReservationNotFoundException("Reserva no encontrada"));

        return reservationMapper.toDto(reservation);
    }

    public List<ReservationResponseDTO> findReservationsByClientId(UUID clientId) throws NoReservationsException {
        if (!clientRepository.existsByIdAndActive(clientId, true)) {
            throw new ClientNotFoundException("Cliente no encontrado");
        }

        System.out.println("🔍 Buscando reservas para clientId: " + clientId);

        List<Reservation> reservations = reservationRepository.findByClientId(clientId);

        System.out.println("📦 Resultado del repository: " + reservations);

        if (reservations == null) {
            System.out.println("⚠️ El repository devolvió null!");
            throw new RuntimeException("El repository devolvió null");
        }

        if (reservations.isEmpty()) {
            System.out.println("ℹ️ No hay reservas para este cliente.");
            throw new NoReservationsException("El cliente aún no ha hecho reservas");
        }


        return reservationMapper.toDto(reservations);
    }


    public List<ReservationResponseDTO> findReservationsByCanchaId(Long canchaId){
        List<Reservation> reservations = reservationRepository.findByCanchaId(canchaId);

        if (reservations.isEmpty()) {
            throw new NoReservationsException("No existen reservas para esa cancha");
        }

        return reservationMapper.toDto(reservations);
    }

    public List<ReservationResponseDTO> findReservationsByEstablishmentId(Long establishmentId){
        List<Reservation> reservations = reservationRepository.findByCanchaEstablishmentId(establishmentId);

        if (reservations.isEmpty()) {
            throw new NoReservationsException("No existen reservas para esa cancha");
        }

        return reservationMapper.toDto(reservations);
    }

    public List<LocalTime> getAvailableHoursByType(Long establishmentId, LocalDate day, String canchaType)
            throws CanchaNotFoundException {

        Establishment establishment = establishmentRepository.findByIdAndActive(establishmentId, true)
                .orElseThrow(() -> new CanchaNotFoundException("Establecimiento no encontrado"));

        List<Cancha> canchas = canchaRepository.findByEstablishmentIdAndActiveAndWorking(establishmentId, true, true)
                .stream()
                .filter(c -> c.getCanchaType().name().equals(canchaType))
                .toList();

        if (canchas.isEmpty()) {
            throw new CanchaNotFoundException("No existen canchas de este tipo");
        }

        List<LocalTime> allHours = new ArrayList<>();
        LocalTime currentHour = establishment.getOpeningHour();
        while (currentHour.isBefore(establishment.getClosingHour())) {
            allHours.add(currentHour);
            currentHour = currentHour.plusHours(1);
        }

        LocalDateTime from = day.atTime(establishment.getOpeningHour());
        LocalDateTime until = day.atTime(establishment.getClosingHour());

        List<Reservation> reservations = new ArrayList<>();
        for (Cancha cancha : canchas) {
            reservations.addAll(
                    reservationRepository.findByCanchaIdAndMatchDateBetweenAndStatus(
                            cancha.getId(), from, until, ReservationStatus.PENDING
                    )
            );
        }

        System.out.println(reservations);

        Map<LocalTime, Long> reservationsCount = reservations.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getMatchDate().toLocalTime()
                                .withMinute(0).withSecond(0).withNano(0),
                        Collectors.counting()
                ));


        int totalCanchas = canchas.size();

        System.out.println("CANTIDAD DE CANCHAS" + " " + totalCanchas);

        System.out.println(allHours.stream()
                .filter(hour -> reservationsCount.getOrDefault(hour, 0L) < totalCanchas)
                .toList());

        return allHours.stream()
                .filter(hour -> reservationsCount.getOrDefault(hour, 0L) < totalCanchas)
                .toList();
    }


    public Reservation completeReservation(Long id){
        Optional<Reservation> reservationOpt = reservationRepository.findById(id);

        if (reservationOpt.isEmpty()) {
            throw new NoReservationsException("La reserva no fue encontrada");
        }

        Reservation reservation = reservationOpt.get();

        reservation.setStatus(ReservationStatus.COMPLETED);

        return reservationRepository.save(reservation);
    }

    public Reservation cancelReservation(Long id){
        Optional<Reservation> reservationOpt = reservationRepository.findById(id);

        if (reservationOpt.isEmpty()) {
            throw new NoReservationsException("La reserva no fue encontrada");
        }

        Reservation reservation = reservationOpt.get();

        reservation.setStatus(ReservationStatus.CANCELED);

        return reservationRepository.save(reservation);
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void completePastReservations() {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> toComplete =
                reservationRepository.findByMatchDateBeforeAndStatus(now, ReservationStatus.PENDING);

        toComplete.forEach(r -> r.setStatus(ReservationStatus.COMPLETED));

        reservationRepository.saveAll(toComplete); // AHORA COMITEA SIN VALIDATION FAILURE
    }


//    @Scheduled(fixedRate = 60000)
//    public void notifyReservationCancel() {
//        List<Reservation> cancelled = reservationRepository.findByStatus(ReservationStatus.CANCELED);
//
//        if (cancelled.isEmpty()) return;
//
//        for (Reservation r : cancelled) {
//            Optional<Client> optionalClient = clientRepository.findByIdAndActive(r.getClient().getId(), true);
//
//            if (optionalClient.isPresent()) {
//                Client client = optionalClient.get();
//                mailService.sendReservationCancelNotice(client.getMail(), r);
//            }
//        }
//    }

}


