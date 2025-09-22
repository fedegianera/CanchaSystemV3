package com.example.CanchaSystem.service;

import com.example.CanchaSystem.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

    @Autowired
    private ReservationRepository reservationRepository;

    public List<Map<String, Object>> getCanchaEarnings(Long ownerId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<Object[]> rawResults = reservationRepository.findCanchaEarnings(ownerId, start, end);
        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] row : rawResults) {
            Map<String, Object> item = new HashMap<>();
            item.put("canchaId", row[0]);
            item.put("canchaName", row[1]);
            item.put("date", row[2]);
            item.put("totalEarnings", row[3]);
            response.add(item);
        }

        return response;
    }

    public List<Map<String, Object>> getBrandEarnings(Long ownerId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<Object[]> rawResults = reservationRepository.findBrandEarnings(ownerId, start, end);
        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] row : rawResults) {
            Map<String, Object> item = new HashMap<>();
            item.put("brandId", row[0]);
            item.put("brandName", row[1]);
            item.put("date", row[2]);
            item.put("totalEarnings", row[3]);
            response.add(item);
        }

        return response;
    }

    public List<Map<String, Object>> getLifetimeCanchaEarnings(Long ownerId) {
        List<Object[]> raw = reservationRepository.getLifetimeCanchaEarnings(ownerId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : raw) {
            Map<String, Object> map = new HashMap<>();
            map.put("canchaId", row[0]);
            map.put("canchaName", row[1]);
            map.put("totalEarnings", row[2]);
            result.add(map);
        }
        return result;
    }

    public List<Map<String, Object>> getLifetimeBrandEarnings(Long ownerId) {
        List<Object[]> raw = reservationRepository.getLifetimeBrandEarnings(ownerId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : raw) {
            Map<String, Object> map = new HashMap<>();
            map.put("brandId", row[0]);
            map.put("brandName", row[1]);
            map.put("totalEarnings", row[2]);
            result.add(map);
        }
        return result;
    }

    public List<Map<String, Object>> getAllLifetimeBrandEarnings() {
        List<Object[]> raw = reservationRepository.getAllLifetimeBrandEarnings();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : raw) {
            Map<String, Object> map = new HashMap<>();
            map.put("brandId", row[0]);
            map.put("brandName", row[1]);
            map.put("totalEarnings", row[2]);
            result.add(map);
        }

        return result;
    }

    public List<Map<String, Object>> getAllLifetimeCanchaEarnings() {
        List<Object[]> raw = reservationRepository.getAllLifetimeCanchaEarnings();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : raw) {
            Map<String, Object> map = new HashMap<>();
            map.put("canchaId", row[0]);
            map.put("canchaName", row[1]);
            map.put("totalEarnings", row[2]);
            result.add(map);
        }

        return result;
    }



}
