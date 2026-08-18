package com.example.CanchaSystem.repository;

import com.example.CanchaSystem.model.Reservation;
import com.example.CanchaSystem.model.ReservationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    boolean existsById(Long id);
    boolean existsByMatchDateAndCanchaIdAndStatus(LocalDateTime date,Long canchaId, ReservationStatus status);
    Optional<Reservation> findById(Long id);
    long countByStatus(ReservationStatus status);
    long countByMatchDateBetween(LocalDateTime from, LocalDateTime until);
    long countByMatchDateBetweenAndStatus(LocalDateTime from, LocalDateTime until, ReservationStatus status);

    List<Reservation> findByCanchaId(Long canchaId);
    List<Reservation> findByClientId(UUID clientId);
    List<Reservation> findByCanchaIdAndMatchDateBetween(Long canchaId, LocalDateTime from, LocalDateTime until);
    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByMatchDateBetweenAndStatus(LocalDateTime from, LocalDateTime until,ReservationStatus status);
    List<Reservation> findByMatchDateBeforeAndStatus(LocalDateTime now, ReservationStatus status);
    List<Reservation> findByCanchaIdAndMatchDateBetweenAndStatus(Long canchaId, LocalDateTime start, LocalDateTime end, ReservationStatus status);

    List<Reservation> findByCanchaEstablishmentId(Long establishmentId);

    List<Reservation> findByCanchaIdInAndMatchDateBetweenAndStatus(
            List<Long> canchaIds,
            LocalDateTime from,
            LocalDateTime until,
            ReservationStatus status
    );

    @Query("""
    SELECT r.matchDate
    FROM Reservation r
    WHERE r.cancha.id IN :canchaIds
    AND r.matchDate BETWEEN :from AND :until
    AND r.status = :status
    """)
    List<LocalDateTime> findMatchDatesByCanchaIdsAndDateRange(
            @Param("canchaIds") List<Long> canchaIds,
            @Param("from") LocalDateTime from,
            @Param("until") LocalDateTime until,
            @Param("status") ReservationStatus status
    );



    @Query("SELECT MIN(r.matchDate) FROM Reservation r")
    Optional<LocalDateTime> findEarliestMatchDate();

    @Query("""
    SELECT r.cancha.establishment.id, r.cancha.establishment.name, COUNT(r)
    FROM Reservation r
    WHERE r.cancha.establishment.active = true
    GROUP BY r.cancha.establishment.id, r.cancha.establishment.name
    ORDER BY COUNT(r) DESC
    """)
    List<Object[]> findTopEstablishmentsByReservationCount(Pageable pageable);

    @Query("""
    SELECT r.cancha.establishment.id, r.cancha.establishment.name, COUNT(r)
    FROM Reservation r
    WHERE r.matchDate BETWEEN :from AND :until
    AND r.cancha.establishment.active = true
    GROUP BY r.cancha.establishment.id, r.cancha.establishment.name
    ORDER BY COUNT(r) DESC
    """)
    List<Object[]> findTopEstablishmentsByReservationCountInPeriod(
            @Param("from") LocalDateTime from,
            @Param("until") LocalDateTime until,
            Pageable pageable
    );

    // --- Estadísticas para el owner ---

    @Query("""
    SELECT r.cancha.establishment.id, COUNT(r)
    FROM Reservation r
    WHERE r.cancha.establishment.id IN :establishmentIds
    GROUP BY r.cancha.establishment.id
    """)
    List<Object[]> countReservationsByEstablishment(@Param("establishmentIds") List<Long> establishmentIds);

    @Query("""
    SELECT FUNCTION('HOUR', r.matchDate), COUNT(r)
    FROM Reservation r
    WHERE r.cancha.establishment.id IN :establishmentIds
    GROUP BY FUNCTION('HOUR', r.matchDate)
    ORDER BY FUNCTION('HOUR', r.matchDate)
    """)
    List<Object[]> countReservationsByHour(@Param("establishmentIds") List<Long> establishmentIds);

    @Query("""
    SELECT r.cancha.establishment.id, r.status, COUNT(r)
    FROM Reservation r
    WHERE r.cancha.establishment.id IN :establishmentIds
    GROUP BY r.cancha.establishment.id, r.status
    """)
    List<Object[]> countReservationsByEstablishmentAndStatus(@Param("establishmentIds") List<Long> establishmentIds);

    @Query("""
    SELECT r.cancha.establishment.id, SUM(r.cancha.totalAmount)
    FROM Reservation r
    WHERE r.cancha.establishment.id IN :establishmentIds
    AND r.status = :status
    GROUP BY r.cancha.establishment.id
    """)
    List<Object[]> sumRevenueByEstablishmentAndStatus(
            @Param("establishmentIds") List<Long> establishmentIds,
            @Param("status") ReservationStatus status
    );

}