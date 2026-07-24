package com.example.CanchaSystem.service;

import com.example.CanchaSystem.dto.response.CanchaTypeCountDTO;
import com.example.CanchaSystem.dto.response.StatsResponseDTO;
import com.example.CanchaSystem.dto.response.TopEstablishmentDTO;
import com.example.CanchaSystem.model.ReservationStatus;
import com.example.CanchaSystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class StatsAdminService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CanchaRepository canchaRepository;

    @Autowired
    private EstablishmentRepository establishmentRepository;

    @Autowired
    private CanchaBrandRepository brandRepository;

    private static final int TOP_ESTABLISHMENTS_LIMIT = 5;

    public StatsResponseDTO getGeneralStats() {
        long total = reservationRepository.count();
        long completed = reservationRepository.countByStatus(ReservationStatus.COMPLETED);
        long canceled = reservationRepository.countByStatus(ReservationStatus.CANCELED);
        long pending = reservationRepository.countByStatus(ReservationStatus.PENDING);

        long totalOwners = ownerRepository.countByActive(true);
        long totalClients = clientRepository.countByActive(true);
        long totalActiveCanchas = canchaRepository.countByActive(true);
        long totalActiveEstablishments = establishmentRepository.countByActive(true);
        long totalActiveBrands = brandRepository.countByActive(true);

        double avgPerDay = calculateAverageGeneral(total);
        double cancellationRate = calculateCancellationRate(total, canceled);

        List<CanchaTypeCountDTO> canchasByType = mapCanchaTypeCounts(
                canchaRepository.countActiveCanchasByType()
        );

        Pageable top5 = PageRequest.of(0, TOP_ESTABLISHMENTS_LIMIT);
        List<TopEstablishmentDTO> topEstablishments = mapTopEstablishments(
                reservationRepository.findTopEstablishmentsByReservationCount(top5)
        );

        return new StatsResponseDTO(
                total, completed, canceled, pending,
                totalOwners, totalClients,
                totalActiveCanchas, totalActiveEstablishments, totalActiveBrands,
                avgPerDay, cancellationRate,
                canchasByType, topEstablishments
        );
    }

    public StatsResponseDTO getStatsByPeriod(LocalDateTime from, LocalDateTime until) {
        long total = reservationRepository.countByMatchDateBetween(from, until);
        long completed = reservationRepository.countByMatchDateBetweenAndStatus(from, until, ReservationStatus.COMPLETED);
        long canceled = reservationRepository.countByMatchDateBetweenAndStatus(from, until, ReservationStatus.CANCELED);
        long pending = reservationRepository.countByMatchDateBetweenAndStatus(from, until, ReservationStatus.PENDING);

        // Owners, clients, canchas activas, establecimientos y marcas no dependen del período
        long totalOwners = ownerRepository.countByActive(true);
        long totalClients = clientRepository.countByActive(true);
        long totalActiveCanchas = canchaRepository.countByActive(true);
        long totalActiveEstablishments = establishmentRepository.countByActive(true);
        long totalActiveBrands = brandRepository.countByActive(true);

        double avgPerDay = calculateAveragePeriod(total, from, until);
        double cancellationRate = calculateCancellationRate(total, canceled);

        List<CanchaTypeCountDTO> canchasByType = mapCanchaTypeCounts(
                canchaRepository.countActiveCanchasByType()
        );

        Pageable top5 = PageRequest.of(0, TOP_ESTABLISHMENTS_LIMIT);
        List<TopEstablishmentDTO> topEstablishments = mapTopEstablishments(
                reservationRepository.findTopEstablishmentsByReservationCountInPeriod(from, until, top5)
        );

        return new StatsResponseDTO(
                total, completed, canceled, pending,
                totalOwners, totalClients,
                totalActiveCanchas, totalActiveEstablishments, totalActiveBrands,
                avgPerDay, cancellationRate,
                canchasByType, topEstablishments
        );
    }

    private double calculateAverageGeneral(long total) {
        if (total == 0) return 0.0;

        Optional<LocalDateTime> earliest = reservationRepository.findEarliestMatchDate();
        if (earliest.isEmpty()) return 0.0;

        long days = ChronoUnit.DAYS.between(earliest.get(), LocalDateTime.now());
        days = Math.max(days, 1);

        return round2((double) total / days);
    }

    private double calculateAveragePeriod(long total, LocalDateTime from, LocalDateTime until) {
        if (total == 0) return 0.0;

        long days = ChronoUnit.DAYS.between(from, until);
        days = Math.max(days, 1);

        return round2((double) total / days);
    }

    private double calculateCancellationRate(long total, long canceled) {
        if (total == 0) return 0.0;
        return round2(((double) canceled / total) * 100);
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private List<CanchaTypeCountDTO> mapCanchaTypeCounts(List<Object[]> rows) {
        return rows.stream()
                .map(row -> new CanchaTypeCountDTO(row[0].toString(), (Long) row[1]))
                .toList();
    }

    private List<TopEstablishmentDTO> mapTopEstablishments(List<Object[]> rows) {
        return rows.stream()
                .map(row -> new TopEstablishmentDTO(
                        (Long) row[0],
                        (String) row[1],
                        (Long) row[2]
                ))
                .toList();
    }
}