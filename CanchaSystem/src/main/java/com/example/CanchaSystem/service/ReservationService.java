package com.example.CanchaSystem.service;


import com.example.CanchaSystem.dto.request.ReservationRequestDTO;
import com.example.CanchaSystem.dto.response.CanchaResponseDTO;
import com.example.CanchaSystem.dto.response.EstablishmentResponseDTO;
import com.example.CanchaSystem.dto.response.ReservationResponseDTO;
import com.example.CanchaSystem.exception.cancha.CanchaNotFoundException;
import com.example.CanchaSystem.exception.client.ClientNotFoundException;
import com.example.CanchaSystem.exception.client.NotEnoughMoneyException;
import com.example.CanchaSystem.exception.owner.OwnerNotFoundException;
import com.example.CanchaSystem.exception.reservation.IllegalReservationDateException;
import com.example.CanchaSystem.exception.reservation.NoReservationsException;
import com.example.CanchaSystem.exception.reservation.ReservationNotFoundException;
import com.example.CanchaSystem.model.*;
import com.example.CanchaSystem.repository.CanchaRepository;
import com.example.CanchaSystem.repository.ClientRepository;
import com.example.CanchaSystem.repository.EstablishmentRepository;
import com.example.CanchaSystem.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
                    .deposit(reservationDTO.deposit())
                    .status(ReservationStatus.PENDING)
                    .build();

            return reservation;
        } else
            throw new IllegalReservationDateException("La fecha ya esta reservada");
    }

    public List<Reservation> getAllReservations() throws NoReservationsException {
        if(!reservationRepository.findAll().isEmpty()){
            return reservationRepository.findAll();
        }else
            throw new NoReservationsException("Todavia no hay reservas registradas");


    }

    public Reservation updateReservation(Reservation reservation) throws ReservationNotFoundException {
        Reservation existing = reservationRepository.findById(reservation.getId())
                .orElseThrow(() ->  new ReservationNotFoundException("Reserva no encontrada"));
        existing.setStatus(reservation.getStatus());
        existing.setMatchDate(reservation.getMatchDate());
        return reservationRepository.save(existing);

    }

    public void deleteReservation(Long id) throws ReservationNotFoundException{
        if (reservationRepository.existsById(id)) {
            reservationRepository.deleteById(id);
        }else
            throw new ReservationNotFoundException("Reserva no encontrada");

    }

    public Reservation findReservationById(Long id) throws ReservationNotFoundException {
        return reservationRepository.findById(id).orElseThrow(()-> new ReservationNotFoundException("Reserva no encontrada"));
    }

    public List<Reservation> findReservationsByClient(String username) throws NoReservationsException {
        Optional<Client> clientOpt = clientRepository.findByUsernameAndActive(username, true);

        if (clientOpt.isEmpty()) {
            throw new ClientNotFoundException("Cliente no encontrado");
        }

        Client client = clientOpt.get();

        List<Reservation> reservations = reservationRepository.findByClientId(client.getId());

        if (!reservations.isEmpty()) {
            return reservations;
        } else {
            throw new NoReservationsException("Todavia no hay reservas hechas por el cliente");
        }

    }

    public List<Reservation> findReservationsByCanchaId(Long canchaId){
        return reservationRepository.findByCanchaId(canchaId);
    }

    //EN DUDA
    public Map<String, List<LocalTime>> getAvailableHours(Long establishmentId, LocalDate day) throws CanchaNotFoundException {
        EstablishmentResponseDTO establishment = establishmentRepository.findById(establishmentId)
                .orElseThrow(() -> new CanchaNotFoundException("Establecimiento no encontrado"));

        List<CanchaResponseDTO> canchas = canchaRepository.findByEstablishmentIdAndActiveAndWorking(establishmentId, true, true);
        if (canchas.isEmpty()) {
            throw new CanchaNotFoundException("Cancha/s inexistente/s");
        }

        Map<CanchaType, List<CanchaResponseDTO>> groupedByType = new HashMap<>();
        for (CanchaResponseDTO cancha : canchas) {
            groupedByType.computeIfAbsent(cancha.canchaType(), k -> new ArrayList<>()).add(cancha);
        }

        List<LocalTime> allHours = new ArrayList<>();
        LocalTime currentHour = establishment.openingHour();
        while (currentHour.isBefore(establishment.closingHour())) {
            allHours.add(currentHour);
            currentHour = currentHour.plusHours(1);
        }

        LocalDateTime from = day.atTime(establishment.openingHour());
        LocalDateTime until = day.atTime(establishment.closingHour());

        Map<String, List<LocalTime>> availableHoursMap = new HashMap<>();

        for (Map.Entry<CanchaType, List<CanchaResponseDTO>> entry : groupedByType.entrySet()) {
            CanchaType type = entry.getKey();
            List<CanchaResponseDTO> sameTypeCanchas = entry.getValue();

            List<ReservationResponseDTO> reservations = new ArrayList<>();
            for (CanchaResponseDTO cancha : sameTypeCanchas) {
                reservations.addAll(reservationRepository.findByCanchaIdAndMatchDateBetweenAndStatus(
                        cancha.id(), from, until, ReservationStatus.PENDING));
            }

            Map<LocalTime, Long> reservationsCount = reservations.stream()
                    .collect(Collectors.groupingBy(res -> res.matchDate().toLocalTime(), Collectors.counting()));

            int totalCanchas = sameTypeCanchas.size();

            List<LocalTime> availableHours = allHours.stream()
                    .filter(hour -> reservationsCount.getOrDefault(hour, 0L) < totalCanchas)
                    .toList();

            availableHoursMap.put(type.name(), availableHours);
        }

        return availableHoursMap;
    }


    public Reservation completeReservation(Reservation reservation){
        if(reservationRepository.existsById(reservation.getId())){
            reservation.setStatus(ReservationStatus.COMPLETED);
            return reservationRepository.save(reservation);
        }else
            throw new ReservationNotFoundException("Reserva no encontrada");
    }

    public Reservation cancelReservation(Reservation reservation){
        if(reservationRepository.existsById(reservation.getId())){
            reservation.setStatus(ReservationStatus.CANCELED);
            return reservationRepository.save(reservation);
        }else
            throw new ReservationNotFoundException("Reserva no encontrada");
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


