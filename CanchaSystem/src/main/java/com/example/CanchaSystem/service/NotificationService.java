package com.example.CanchaSystem.service;

import com.example.CanchaSystem.exception.misc.MissingDataException;
import com.example.CanchaSystem.model.Reservation;
import com.example.CanchaSystem.model.ReservationStatus;
import com.example.CanchaSystem.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    MailService mailService;

    @Autowired
    ReservationRepository reservationRepository;

    @Scheduled(cron = "0 0 7 * * ?")
    public void notifyUpcomingMatches() throws MissingDataException {
        LocalDateTime tomorrowStart = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime tomorrowEnd = tomorrowStart.withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        List<Reservation> reservations = reservationRepository.findByMatchDateBetweenAndStatus(tomorrowStart, tomorrowEnd, ReservationStatus.PENDING);

        for (Reservation reservation : reservations) {
            if (reservation.getClient() != null && reservation.getClient().getMail() != null &&
                    reservation.getCancha() != null) {
                mailService.sendReminder(reservation.getClient().getMail(), reservation);
            } else {
               throw new MissingDataException("Los datos de la reserva id: "+ reservation.getId() + " son inválidos");
            }
        }
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void completePastReservations() {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> toComplete = reservationRepository.findByMatchDateBeforeAndStatus(now, ReservationStatus.PENDING);

        for (Reservation reservation : toComplete) {
            reservation.setStatus(ReservationStatus.COMPLETED);
        }

        reservationRepository.saveAll(toComplete);
    }
}
