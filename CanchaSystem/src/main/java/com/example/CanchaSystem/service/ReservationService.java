package com.example.CanchaSystem.service;


import com.example.CanchaSystem.Mapper.CanchaMapper;
import com.example.CanchaSystem.Mapper.EstablishmentMapper;
import com.example.CanchaSystem.Mapper.ReservationMapper;
import com.example.CanchaSystem.dto.request.ReservationRequestDTO;
import com.example.CanchaSystem.dto.response.CanchaResponseDTO;
import com.example.CanchaSystem.dto.response.EstablishmentResponseDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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


    public Reservation insertReservation(ReservationRequestDTO reservationDTO, Authentication auth)
            throws IllegalReservationDateException {
        if(!reservationRepository.existsBymatchDateAndCanchaId(reservationDTO.matchDate(), reservationDTO.canchaId())) {
            String username = auth.getName();

            Client client = clientRepository.findByUsernameAndActive(username, true)
                    .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado"));

            Cancha cancha = canchaRepository.findById(reservationDTO.canchaId())
                    .orElseThrow(() -> new CanchaNotFoundException("Cancha no encontrada"));

            Reservation reservation = Reservation.builder()
                    .client(client)
                    .cancha(cancha)
                    .reservationDate(reservationDTO.reservationDate())
                    .matchDate(reservationDTO.matchDate())
                    .status(ReservationStatus.PENDING)
                    .build();

            return reservationRepository.save(reservation);
        } else
            throw new IllegalReservationDateException("La fecha ya esta reservada");
    }

    public List<ReservationResponseDTO> getAllReservations() throws NoReservationsException {
        List<Reservation> reservations = reservationRepository.findAll();

        if (reservations.isEmpty()) {
            throw new NoReservationsException("Aun no hay reservas hechas");
        }


        return reservationMapper.toDto(reservations);
    }

    public Reservation updateReservation(Long id, ReservationRequestDTO reservationRequestDTO) throws ReservationNotFoundException {
        Optional<Reservation> reservationOpt = reservationRepository.findById(id);

        if (reservationOpt.isEmpty()) {
            throw new ReservationNotFoundException("Reserva no encontrada");
        }

        Reservation reservation = reservationOpt.get();

        reservation.setStatus(reservationRequestDTO.status());
        reservation.setMatchDate(reservationRequestDTO.matchDate());

        return reservationRepository.save(reservation);
    }

    public Reservation findReservationById(Long id) throws ReservationNotFoundException {
        return reservationRepository.findById(id).orElseThrow(()-> new ReservationNotFoundException("Reserva no encontrada"));
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

        // Logueamos el contenido de la primera reserva para detectar relaciones nulas
        Reservation first = reservations.get(0);
        System.out.println("🧩 Primera reserva:");
        System.out.println("   ID: " + first.getId());
        System.out.println("   Cliente: " + (first.getClient() != null ? first.getClient().getId() : "NULL"));
        System.out.println("   Cancha: " + (first.getCancha() != null ? first.getCancha().getId() : "NULL"));

        List<ReservationResponseDTO> response = reservationMapper.toDto(reservations);

        System.out.println("✅ Conversion exitosa. Total DTOs: " + response.size());

        return response;
    }


    public List<ReservationResponseDTO> findReservationsByCanchaId(Long canchaId){
        List<Reservation> reservations = reservationRepository.findByCanchaId(canchaId);

        if (reservations.isEmpty()) {
            throw new NoReservationsException("No existen reservas para esa cancha");
        }

        return reservationMapper.toDto(reservations);
    }

    //EN DUDA
    public Map<String, List<LocalTime>> getAvailableHours(Long establishmentId, LocalDate day) throws CanchaNotFoundException {
        Establishment establishment = establishmentRepository.findByIdAndActive(establishmentId, true)
                .orElseThrow(() -> new CanchaNotFoundException("Establecimiento no encontrado"));

        EstablishmentResponseDTO establishmentDTO = establishmentMapper.toDto(establishment);

        List<Cancha> canchas = canchaRepository.findByEstablishmentIdAndActiveAndWorking(establishmentId, true, true);
        if (canchas.isEmpty()) {
            throw new CanchaNotFoundException("Cancha/s inexistente/s");
        }

        List<CanchaResponseDTO> canchasDto = canchaMapper.toDto(canchas);

        Map<CanchaType, List<CanchaResponseDTO>> groupedByType = new HashMap<>();
        for (CanchaResponseDTO cancha : canchasDto) {
            groupedByType.computeIfAbsent(cancha.canchaType(), k -> new ArrayList<>()).add(cancha);
        }

        List<LocalTime> allHours = new ArrayList<>();
        LocalTime currentHour = establishmentDTO.openingHour();
        while (currentHour.isBefore(establishmentDTO.closingHour())) {
            allHours.add(currentHour);
            currentHour = currentHour.plusHours(1);
        }

        LocalDateTime from = day.atTime(establishmentDTO.openingHour());
        LocalDateTime until = day.atTime(establishmentDTO.closingHour());

        Map<String, List<LocalTime>> availableHoursMap = new HashMap<>();

        for (Map.Entry<CanchaType, List<CanchaResponseDTO>> entry : groupedByType.entrySet()) {
            CanchaType type = entry.getKey();
            List<CanchaResponseDTO> sameTypeCanchas = entry.getValue();

            List<Reservation> reservations = new ArrayList<>();

            for (CanchaResponseDTO cancha : sameTypeCanchas) {
                reservations.addAll(reservationRepository.findByCanchaIdAndMatchDateBetweenAndStatus(
                        cancha.id(), from, until, ReservationStatus.PENDING));
            }

            Map<LocalTime, Long> reservationsCount = reservations.stream()
                    .collect(Collectors.groupingBy(
                            r -> r.getMatchDate().toLocalTime().truncatedTo(ChronoUnit.HOURS),
                            Collectors.counting()
                    ));


            int totalCanchas = sameTypeCanchas.size();

            List<LocalTime> availableHours = allHours.stream()
                    .filter(hour -> reservationsCount.getOrDefault(hour, 0L) < totalCanchas)
                    .toList();

            availableHoursMap.put(type.name(), availableHours);
        }

        return availableHoursMap;
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


