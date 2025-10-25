package com.example.CanchaSystem.service;


import com.example.CanchaSystem.dto.request.ReservationRequestDTO;
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
import com.example.CanchaSystem.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<LocalTime> getAvailableHours(Long canchaId, LocalDate day) throws CanchaNotFoundException {
        Cancha canchaAux = canchaRepository.findById(canchaId)
                .orElseThrow(() -> new CanchaNotFoundException("Cancha no encontrada"));

        LocalTime firstHour = canchaAux.getEstablishment().getOpeningHour();
        List<LocalTime> allHours = new ArrayList<>();

        while (firstHour.isBefore(canchaAux.getEstablishment().getClosingHour())){
            allHours.add(firstHour);
            firstHour = firstHour.plusHours(1);
        }

        LocalDateTime from = day.atTime(canchaAux.getEstablishment().getOpeningHour());
        LocalDateTime until = day.atTime(canchaAux.getEstablishment().getClosingHour());

        List<Reservation> reservations = reservationRepository
                .findByCanchaIdAndMatchDateBetweenAndStatus(canchaId,from,until,ReservationStatus.PENDING);

        Set<LocalTime> reservedHours = reservations.stream()
                .map(reservation -> reservation.getMatchDate().toLocalTime())
                .collect(Collectors.toSet());

        return allHours.stream()
                .filter(availableHour -> !reservedHours.contains(availableHour))
                .collect(Collectors.toList());
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


