package com.example.CanchaSystem.service;


import com.example.CanchaSystem.exception.cancha.CanchaNotFoundException;
import com.example.CanchaSystem.exception.client.ClientNotFoundException;
import com.example.CanchaSystem.exception.reservation.IllegalReservationDateException;
import com.example.CanchaSystem.exception.reservation.NoReservationsException;
import com.example.CanchaSystem.exception.reservation.ReservationNotFoundException;
import com.example.CanchaSystem.model.*;
import com.example.CanchaSystem.repository.CanchaRepository;
import com.example.CanchaSystem.repository.ClientRepository;
import com.example.CanchaSystem.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    private MailService mailService;


    public Reservation insertReservation(Reservation reservation)
            throws IllegalReservationDateException {
        if(!reservationRepository.existsBymatchDateAndCanchaId(reservation.getMatchDate(),reservation.getCancha().getId()))
            return reservationRepository.save(reservation);
        else
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

    public List<LocalTime> getAvailableHours(Long canchaId, LocalDate day)throws CanchaNotFoundException{
        Cancha canchaAux = canchaRepository.findById(canchaId)
                .orElseThrow(() -> new CanchaNotFoundException("Cancha no encontrada"));

        LocalTime firstHour = canchaAux.getOpeningHour();
        List<LocalTime> allHours = new ArrayList<>();

        while (firstHour.isBefore(canchaAux.getClosingHour())){
            allHours.add(firstHour);
            firstHour = firstHour.plusHours(1);
        }

        LocalDateTime from = day.atTime(canchaAux.getOpeningHour());
        LocalDateTime until = day.atTime(canchaAux.getClosingHour());

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

    public List<Reservation> getReservationsByBrandId(Long brandId) throws NoReservationsException{
        List<Reservation> reservations = reservationRepository.findAllByBrandId(brandId);
        if (reservations.isEmpty())
            throw new NoReservationsException("Todavía no hay reseñas hechas");
        return reservations;
    }

    public List<Reservation> getReservationsByOwnerId(Long ownerId) throws NoReservationsException{
        List<Reservation> reservations = reservationRepository.findAllByOwnerId(ownerId);
        if (reservations.isEmpty())
            throw new NoReservationsException("Todavía no hay reseñas hechas");
        return reservations;
    }

    @Scheduled(fixedRate = 60000)
    public void finishPastReservations() {
        List<Reservation> expired = reservationRepository.findByStatusAndMatchDateBefore(
                ReservationStatus.PENDING,
                LocalDateTime.now()
        );

        for (Reservation r : expired) {
            r.setStatus(ReservationStatus.COMPLETED);
        }

        if (!expired.isEmpty()) {
            reservationRepository.saveAll(expired);
        }
    }

    @Scheduled(fixedRate = 60000)
    public void notifyReservationCancel() {
        List<Reservation> cancelled = reservationRepository.findByStatus(ReservationStatus.CANCELED);

        if (cancelled.isEmpty()) return;

        for (Reservation r : cancelled) {
            Optional<Client> optionalClient = clientRepository.findByIdAndActive(r.getClient().getId(), true);

            if (optionalClient.isPresent()) {
                Client client = optionalClient.get();
                mailService.sendReservationCancelNotice(client.getMail(), r);
            }
        }
    }


    public Optional<Owner> getOwnerFromReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .map(Reservation::getCancha)
                .map(Cancha::getBrand)
                .map(CanchaBrand::getOwner);
    }


}


