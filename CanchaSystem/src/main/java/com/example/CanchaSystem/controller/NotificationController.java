package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @GetMapping("/notifications/send-reminders")
    public String sendReminders() {
        notificationService.notifyUpcomingMatches();
        return "Recordatorios enviados.";
    }

    @GetMapping("/notifications/complete-reservations")
    public String completeReservations() {
        notificationService.completePastReservations();
        return "Reservas completadas actualizadas.";
    }
}
