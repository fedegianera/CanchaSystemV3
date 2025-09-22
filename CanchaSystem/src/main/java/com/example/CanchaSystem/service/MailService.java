package com.example.CanchaSystem.service;

import com.example.CanchaSystem.exception.client.ClientNotFoundException;
import com.example.CanchaSystem.model.Client;
import com.example.CanchaSystem.model.OwnerRequest;
import com.example.CanchaSystem.model.Reservation;
import com.example.CanchaSystem.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ClientRepository clientRepository;

    @Async
    public void sendReminder(String to, Reservation reservation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Recordatorio de partido mañana");
        message.setText(String.format(
                "Hola %s," +
                        "\n\n" +
                        "Te recordamos que mañana tenés un partido reservado en la cancha %s a las %s." +
                        "\n\n¡Éxitos!",
                reservation.getClient().getName(),
                reservation.getCancha().getName(),
                reservation.getMatchDate().toString()
        ));
        message.setFrom("canchasystem@gmail.com");

        mailSender.send(message);
    }

    @Async
    public void sendReservationNoticeOwner(String to, Reservation reservation) {

        if (reservation == null ||
                reservation.getClient() == null ||
                reservation.getCancha() == null ||
                reservation.getCancha().getBrand() == null ||
                reservation.getCancha().getBrand().getOwner() == null) {
            throw new IllegalStateException("La reserva no tiene datos completos para enviar el mail.");
        }


        String body = String.format("""
        Hola %s,

        Te avisamos que se confirmó una reserva de %s en la cancha "%s".

        Fecha de creación de la reserva: %s
        Fecha del partido: %s

        Saludos,
        CanchaSystem.
        """,
                reservation.getCancha().getBrand().getOwner().getName(),
                reservation.getClient().getName(),
                reservation.getCancha().getName(),
                reservation.getReservationDate(),
                reservation.getMatchDate()
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Reserva confirmada en " + reservation.getCancha().getName());
        message.setText(body);
        message.setFrom("canchasystem@gmail.com");


        mailSender.send(message);
    }

    @Async
    public void sendReservationCancelNotice(String to, Reservation reservation) {

        if (reservation == null ||
                reservation.getClient() == null ||
                reservation.getCancha() == null ||
                reservation.getCancha().getBrand() == null ||
                reservation.getCancha().getBrand().getOwner() == null) {
            throw new IllegalStateException("La reserva no tiene datos completos para enviar el mail.");
        }


        String body = String.format("""
        Hola %s,

        Nos apena avisarte que se canceló tu reserva en la cancha "%s"
        del dia %s.

        Saludos,
        CanchaSystem.
        """,
                reservation.getClient().getName(),
                reservation.getCancha().getName(),
                reservation.getMatchDate()
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Reserva cancelada en " + reservation.getCancha().getName());
        message.setText(body);
        message.setFrom("canchasystem@gmail.com");


        mailSender.send(message);
    }

    @Async
    public void sendReservationNoticeClient(String to, Reservation reservation) {

        if (reservation == null ||
                reservation.getClient() == null ||
                reservation.getCancha() == null ||
                reservation.getCancha().getBrand() == null ||
                reservation.getCancha().getBrand().getOwner() == null) {
            throw new IllegalStateException("La reserva no tiene datos completos para enviar el mail.");
        }


        String body = String.format("""
        Hola %s,

        Te avisamos que se confirmó tu reserva en la cancha "%s".

        Fecha de creación de la reserva: %s
        Fecha del partido: %s

        Saludos,
        CanchaSystem.
        """,
                reservation.getClient().getName(),
                reservation.getCancha().getName(),
                reservation.getReservationDate(),
                reservation.getMatchDate()
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Reserva confirmada en " + reservation.getCancha().getName());
        message.setText(body);
        message.setFrom("canchasystem@gmail.com");

        mailSender.send(message);
    }

    @Async
    public void sendRequestNoticeToClient(String to, OwnerRequest request) {

        if (request == null ||
                request.getClient() == null ||
                request.getRequestDate() == null ) {
            throw new IllegalStateException("La solicitud no tiene datos completos para enviar el mail.");
        }

        Client sendTo = clientRepository.findByIdAndActive(request.getClient().getId(), true)
                .orElseThrow(() -> new ClientNotFoundException("El cliente no se encontro"));

        String body = String.format("""
        Hola %s,

        Te avisamos que se envió tu solicitud.

        Fecha de creación de la solicitud: %s

        Saludos,
        CanchaSystem.
        """,
                sendTo.getName(),
                request.getRequestDate()
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Solicitud para ser dueño");
        message.setText(body);
        message.setFrom("canchasystem@gmail.com");

        mailSender.send(message);
    }

    @Async
    public void sendRequestApprovedStatusUpdateToClient(String to, OwnerRequest request) {

        if (request == null ||
                request.getClient() == null ||
                request.getRequestDate() == null ||
                request.getStatus() == null) {
            throw new IllegalStateException("La solicitud no tiene datos completos para enviar el mail.");
        }

        Client sendTo = clientRepository.findByIdAndActive(request.getClient().getId(), true)
                .orElseThrow(() -> new ClientNotFoundException("El cliente no se encontro"));

        String body = String.format("""
        Hola %s,

        Te avisamos que tu solicitud fue aprobada.

        Tu cuenta con poca antelación se va a habilitar.
        Es un gusto contar con vos 
        
        Saludos,
        CanchaSystem.
        """,
                sendTo.getName()
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Solicitud aprobada");
        message.setText(body);
        message.setFrom("canchasystem@gmail.com");

        mailSender.send(message);
    }

    @Async
    public void sendRequestDeniedStatusUpdateToClient(String to, OwnerRequest request) {

        if (request == null ||
                request.getClient() == null ||
                request.getRequestDate() == null ||
                request.getStatus() == null) {
            throw new IllegalStateException("La solicitud no tiene datos completos para enviar el mail.");
        }

        Client sendTo = clientRepository.findByIdAndActive(request.getClient().getId(), true)
                .orElseThrow(() -> new ClientNotFoundException("El cliente no se encontro"));

        String body = String.format("""
        Hola %s,

        Nos apena informar que tu solicitud fue denegada.

        Saludos,
        CanchaSystem.
        """,
                sendTo.getName()
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Solicitud denegada");
        message.setText(body);
        message.setFrom("canchasystem@gmail.com");

        mailSender.send(message);
    }

}
