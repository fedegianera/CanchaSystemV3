package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.service.StatisticsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
@RequestMapping("/stats/owner/{ownerId}/brand")
public class CanchaBrandStatisticsController {

    @Autowired
    private StatisticsService statsService;

    @GetMapping("/daily")
    public List<Map<String, Object>> getDailyStats(@PathVariable Long ownerId) {
        LocalDate today = LocalDate.now();
        return statsService.getBrandEarnings(ownerId, today, today);
    }

    @GetMapping("/weekly")
    public List<Map<String, Object>> getWeeklyStats(@PathVariable Long ownerId) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1); // Monday
        LocalDate endOfWeek = startOfWeek.plusDays(6); // Sunday
        return statsService.getBrandEarnings(ownerId, startOfWeek, endOfWeek);
    }

    @GetMapping("/monthly")
    public List<Map<String, Object>> getMonthlyStats(@PathVariable Long ownerId) {
        LocalDate today = LocalDate.now();
        LocalDate start = today.withDayOfMonth(1);
        LocalDate end = start.plusMonths(1).minusDays(1);
        return statsService.getBrandEarnings(ownerId, start, end);
    }

    @GetMapping("/yearly")
    public List<Map<String, Object>> getYearlyStats(@PathVariable Long ownerId) {
        LocalDate today = LocalDate.now();
        LocalDate start = LocalDate.of(today.getYear(), 1, 1);
        LocalDate end = LocalDate.of(today.getYear(), 12, 31);
        return statsService.getBrandEarnings(ownerId, start, end);
    }

    @GetMapping("/lifetime")
    public List<Map<String, Object>> getLifetimeCanchaEarnings(@PathVariable Long ownerId) {
        return statsService.getLifetimeBrandEarnings(ownerId);
    }

    @GetMapping("/lifetime/all")
    public List<Map<String, Object>> getLifetimeEarningsForAllBrands(@PathVariable Long ownerId) {
        return statsService.getAllLifetimeBrandEarnings();
    }

}

