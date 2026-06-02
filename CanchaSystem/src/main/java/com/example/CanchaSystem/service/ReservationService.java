package com.example.CanchaSystem.service;

import com.example.CanchaSystem.Mapper.ReservationMapper;
import com.example.CanchaSystem.dto.request.ReservationRequestDTO;
import com.example.CanchaSystem.dto.response.ReservationResponseDTO;
import com.example.CanchaSystem.exception.cancha.CanchaNotFoundException;
import com.example.CanchaSystem.exception.establishment.EstablishmentNotFoundException;
import com.example.CanchaSystem.exception.reservation.IllegalReservationDateException;
import com.example.CanchaSystem.exception.reservation.NoReservationsException;
import com.example.CanchaSystem.exception.reservation.ReservationNotFoundException;
import com.example.CanchaSystem.exception.user.UserNotFoundException;
import com.example.CanchaSystem.model.*;
import com.example.CanchaSystem.repository.CanchaRepository;
import com.example.CanchaSystem.repository.ClientRepository;
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
    private ReservationMapper reservationMapper;
    @Autowired
    private ClientService clientService;
    @Autowired
    private EstablishmentService establishmentService;


    public ReservationResponseDTO insertReservation(ReservationRequestDTO reservationDTO, Authentication auth)
            throws IllegalReservationDateException {
        Client client = clientService.findClientOrThrow(auth.getName());

        LocalDateTime correctedMatchDate = localizeTime(reservationDTO.matchDate());
        LocalDateTime correctedReservationDate = localizeTime(reservationDTO.reservationDate());

        List<Cancha> canchas = canchaRepository.findByEstablishmentIdAndCanchaType(
                reservationDTO.establishmentId(),
                reservationDTO.canchaType()
        );

        if (canchas.isEmpty()) throw new CanchaNotFoundException();

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
                .reservationDate(correctedReservationDate)
                .matchDate(correctedMatchDate)
                .status(ReservationStatus.PENDING)
                .build();

        reservationRepository.save(reservation);

        return reservationMapper.toDto(reservation);
    }

    public Reservation findReservationOrThrow(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
    }

    private LocalDateTime localizeTime(LocalDateTime time) {
        return time.atOffset(ZoneOffset.UTC)
                .withOffsetSameInstant(ZoneOffset.of("-03:00"))
                .toLocalDateTime();
    }

    public List<ReservationResponseDTO> getAllReservations() {
        return reservationMapper.toDto(
                reservationRepository.findAll()
        );
    }

    public ReservationResponseDTO updateReservation(Long id, ReservationRequestDTO reservationRequestDTO) throws ReservationNotFoundException {
        Reservation reservation = findReservationOrThrow(id);

        LocalDateTime correctedMatchDate = localizeTime(reservationRequestDTO.matchDate());

        reservation.setStatus(reservationRequestDTO.status());
        reservation.setMatchDate(correctedMatchDate);

        reservationRepository.save(reservation);
        return reservationMapper.toDto(reservation);
    }

    public ReservationResponseDTO findReservationById(Long id) throws ReservationNotFoundException {
        return reservationMapper.toDto(
                findReservationOrThrow(id)
        );
    }

    public List<ReservationResponseDTO> findReservationsByClientId(UUID clientId) {
        if (!clientRepository.existsByIdAndActive(clientId, true)) {
            throw new UserNotFoundException(clientId, Role.CLIENT);
        }

        return reservationMapper.toDto(
                reservationRepository.findByClientId(clientId)
        );
    }


    public List<ReservationResponseDTO> findReservationsByCanchaId(Long canchaId){
        return reservationMapper.toDto(
                reservationRepository.findByCanchaId(canchaId)
        );
    }

    public List<ReservationResponseDTO> findReservationsByEstablishmentId(Long establishmentId){
        List<Reservation> reservations = reservationRepository.findByCanchaEstablishmentId(establishmentId);

        if (reservations.isEmpty()) {
            throw new NoReservationsException("No existen reservas para esa cancha");
        }

        System.out.println("-----------------------------------------------");
        System.out.println(reservations);

        return reservationMapper.toDto(reservations);
    }

    public List<LocalTime> getAvailableHoursByType(Long establishmentId, LocalDate day, String canchaType)
            throws CanchaNotFoundException, EstablishmentNotFoundException {

        System.out.println("🔍 Step 1: Fetching establishment...");
        Establishment establishment = establishmentService.findEstablishmentOrThrow(establishmentId);

        System.out.println("🔍 Step 2: Fetching canchas...");
        List<Cancha> canchas = canchaRepository.findByEstablishmentIdAndActiveAndWorkingAndCanchaType(
                establishmentId, true, true, CanchaType.valueOf(canchaType)
        );
        if (canchas.isEmpty()) throw new CanchaNotFoundException();

        System.out.println("✅ Found " + canchas.size() + " canchas");

        // Normalizar las horas a minutos y segundos en 0
        LocalTime openingHour = establishment.getOpeningHour()
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        LocalTime closingHour = establishment.getClosingHour()
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        System.out.println("🕐 Opening: " + openingHour + " | Closing: " + closingHour);

        // Validación: evitar bucle infinito
        if (!openingHour.isBefore(closingHour)) {
            throw new IllegalStateException(
                    "Horario de apertura (" + openingHour + ") debe ser antes del horario de cierre (" + closingHour + ")"
            );
        }

        // Generar todas las horas posibles
        List<LocalTime> allHours = new ArrayList<>();
        LocalTime currentHour = openingHour;

        while (currentHour.isBefore(closingHour)) {
            allHours.add(currentHour);
            currentHour = currentHour.plusHours(1);
        }

        System.out.println("✅ Generated " + allHours.size() + " available hours");

        // Obtener IDs de canchas
        List<Long> canchaIds = canchas.stream()
                .map(Cancha::getId)
                .toList();

        LocalDateTime from = day.atTime(openingHour);
        LocalDateTime until = day.atTime(closingHour);

        System.out.println("🔍 Step 3: Fetching reservations...");

        // Obtener SOLO las fechas de las reservas (no entidades completas)
        List<LocalDateTime> matchDates = reservationRepository.findMatchDatesByCanchaIdsAndDateRange(
                canchaIds, from, until, ReservationStatus.PENDING
        );

        System.out.println("✅ Found " + matchDates.size() + " reservations");

        // Contar reservas por hora
        Map<LocalTime, Long> reservationsCount = matchDates.stream()
                .collect(Collectors.groupingBy(
                        dateTime -> dateTime.toLocalTime()
                                .withMinute(0)
                                .withSecond(0)
                                .withNano(0),
                        Collectors.counting()
                ));

        int totalCanchas = canchas.size();
        System.out.println("CANTIDAD DE CANCHAS: " + totalCanchas);

        // Filtrar horas disponibles
        List<LocalTime> availableHours = allHours.stream()
                .filter(hour -> reservationsCount.getOrDefault(hour, 0L) < totalCanchas)
                .toList();

        System.out.println("✅ Available hours: " + availableHours);

        return availableHours;
    }


    public Reservation completeReservation(Long id) {
        Reservation reservation = findReservationOrThrow(id);

        reservation.setStatus(ReservationStatus.COMPLETED);

        return reservationRepository.save(reservation);
    }

    public Reservation cancelReservation(Long id) {
        Reservation reservation = findReservationOrThrow(id);

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


