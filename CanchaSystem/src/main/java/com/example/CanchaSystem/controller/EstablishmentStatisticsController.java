package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.service.EstablishmentStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/establishment/statistics")
public class EstablishmentStatisticsController {

    @Autowired
    private EstablishmentStatisticsService statisticsService;

    @GetMapping("/ratingRanking/{ownerId}")
    ResponseEntity<?> getRatingRanking(@PathVariable UUID ownerId) {
        return ResponseEntity.ok(statisticsService.getEstablishmentsRankedByRating(ownerId));
    }

    @GetMapping("/reservationRanking/{ownerId}")
    ResponseEntity<?> getReservationRanking(@PathVariable UUID ownerId) {
        return ResponseEntity.ok(statisticsService.getEstablishmentsRankedByReservationCount(ownerId));
    }

    @GetMapping("/peakHours/{ownerId}")
    ResponseEntity<?> getPeakHours(@PathVariable UUID ownerId) {
        return ResponseEntity.ok(statisticsService.getPeakHours(ownerId));
    }

    @GetMapping("/cancellationRate/{ownerId}")
    ResponseEntity<?> getCancellationRate(@PathVariable UUID ownerId) {
        return ResponseEntity.ok(statisticsService.getCancellationRateByEstablishment(ownerId));
    }

    @GetMapping("/revenue/{ownerId}")
    ResponseEntity<?> getRevenue(@PathVariable UUID ownerId) {
        return ResponseEntity.ok(statisticsService.getRevenueByEstablishment(ownerId));
    }
}