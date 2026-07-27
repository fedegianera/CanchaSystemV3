package com.example.CanchaSystem.service;

import com.example.CanchaSystem.dto.EstablishmentMetricDTO;
import com.example.CanchaSystem.dto.PeakHourDTO;
import com.example.CanchaSystem.dto.response.EstablishmentResponseDTO;
import com.example.CanchaSystem.model.ReservationStatus;
import com.example.CanchaSystem.repository.ReservationRepository;
import com.example.CanchaSystem.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EstablishmentStatisticsService {

    @Autowired
    private EstablishmentService establishmentService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    /**
     * Ranking de las sucursales del owner por promedio de reviews (de mayor a menor).
     */
    public List<EstablishmentMetricDTO> getEstablishmentsRankedByRating(UUID ownerId) {
        Map<Long, String> namesById = getOwnerEstablishmentNames(ownerId);

        List<Object[]> rows = reviewRepository.getAllEstablishmentAverages();

        return rows.stream()
                .filter(r -> namesById.containsKey((Long) r[0]))
                .map(r -> new EstablishmentMetricDTO(
                        (Long) r[0],
                        namesById.get((Long) r[0]),
                        r[1] != null ? (Double) r[1] : 0.0
                ))
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .toList();
    }

    /**
     * Ranking de las sucursales del owner por cantidad de reservas (de mayor a menor).
     */
    public List<EstablishmentMetricDTO> getEstablishmentsRankedByReservationCount(UUID ownerId) {
        Map<Long, String> namesById = getOwnerEstablishmentNames(ownerId);
        List<Long> establishmentIds = namesById.keySet().stream().toList();

        if (establishmentIds.isEmpty()) return List.of();

        List<Object[]> rows = reservationRepository.countReservationsByEstablishment(establishmentIds);

        return rows.stream()
                .map(r -> new EstablishmentMetricDTO(
                        (Long) r[0],
                        namesById.get((Long) r[0]),
                        ((Long) r[1]).doubleValue()
                ))
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .toList();
    }

    /**
     * Cantidad de reservas agrupadas por hora del partido (matchDate), para todas las
     * sucursales del owner. Sirve para armar el gráfico de barras de horas pico.
     */
    public List<PeakHourDTO> getPeakHours(UUID ownerId) {
        List<Long> establishmentIds = getOwnerEstablishmentNames(ownerId).keySet().stream().toList();

        if (establishmentIds.isEmpty()) return List.of();

        List<Object[]> rows = reservationRepository.countReservationsByHour(establishmentIds);

        return rows.stream()
                .map(r -> new PeakHourDTO(
                        ((Number) r[0]).intValue(),
                        ((Number) r[1]).longValue()
                ))
                .toList();
    }

    /**
     * Tasa de cancelación (0 a 1) por sucursal: reservas CANCELED / total de reservas de esa sucursal.
     */
    public List<EstablishmentMetricDTO> getCancellationRateByEstablishment(UUID ownerId) {
        Map<Long, String> namesById = getOwnerEstablishmentNames(ownerId);
        List<Long> establishmentIds = namesById.keySet().stream().toList();

        if (establishmentIds.isEmpty()) return List.of();

        List<Object[]> rows = reservationRepository.countReservationsByEstablishmentAndStatus(establishmentIds);

        Map<Long, Long> totalByEstablishment = rows.stream()
                .collect(Collectors.groupingBy(
                        r -> (Long) r[0],
                        Collectors.summingLong(r -> (Long) r[2])
                ));

        Map<Long, Long> canceledByEstablishment = rows.stream()
                .filter(r -> r[1] == ReservationStatus.CANCELED)
                .collect(Collectors.toMap(
                        r -> (Long) r[0],
                        r -> (Long) r[2]
                ));

        return establishmentIds.stream()
                .map(id -> {
                    long total = totalByEstablishment.getOrDefault(id, 0L);
                    long canceled = canceledByEstablishment.getOrDefault(id, 0L);
                    double rate = total == 0 ? 0.0 : (double) canceled / total;
                    return new EstablishmentMetricDTO(id, namesById.get(id), rate);
                })
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .toList();
    }

    /**
     * Ingresos por sucursal, contando únicamente reservas COMPLETED.
     */
    public List<EstablishmentMetricDTO> getRevenueByEstablishment(UUID ownerId) {
        Map<Long, String> namesById = getOwnerEstablishmentNames(ownerId);
        List<Long> establishmentIds = namesById.keySet().stream().toList();

        if (establishmentIds.isEmpty()) return List.of();

        List<Object[]> rows = reservationRepository.sumRevenueByEstablishmentAndStatus(
                establishmentIds, ReservationStatus.COMPLETED
        );

        return rows.stream()
                .map(r -> new EstablishmentMetricDTO(
                        (Long) r[0],
                        namesById.get((Long) r[0]),
                        r[1] != null ? (Double) r[1] : 0.0
                ))
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .toList();
    }

    private Map<Long, String> getOwnerEstablishmentNames(UUID ownerId) {
        List<EstablishmentResponseDTO> establishments = establishmentService.getEstablishmentsByOwnerId(ownerId);
        return establishments.stream()
                .collect(Collectors.toMap(
                        EstablishmentResponseDTO::id,
                        EstablishmentResponseDTO::name
                ));
    }
}