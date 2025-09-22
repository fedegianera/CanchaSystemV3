package com.example.CanchaSystem.repository;

import com.example.CanchaSystem.model.Owner;
import com.example.CanchaSystem.model.Reservation;
import com.example.CanchaSystem.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    boolean existsById(Long id);
    boolean existsBymatchDateAndCanchaId(LocalDateTime date,Long canchaId);
    Optional<Reservation> findById(Long id);

    List<Reservation> findByCanchaId(Long canchaId);
    List<Reservation> findByClientId(Long clientId);
    List<Reservation> findByCanchaIdAndMatchDateBetween(Long canchaId, LocalDateTime from, LocalDateTime until);
    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByMatchDateBetweenAndStatus(LocalDateTime from, LocalDateTime until,ReservationStatus status);
    List<Reservation> findByMatchDateBeforeAndStatus(LocalDateTime now, ReservationStatus status);
    List<Reservation> findByCanchaIdAndMatchDateBetweenAndStatus(
            Long canchaId, LocalDateTime start, LocalDateTime end, ReservationStatus status);

    @Query("SELECT r FROM Reservation r " +
            "JOIN r.cancha c " +
            "JOIN c.brand b " +
            "WHERE b.id = :brandId")
    List<Reservation> findAllByBrandId(@Param("brandId") Long brandId);

    @Query("SELECT r FROM Reservation r " +
            "JOIN r.cancha c " +
            "JOIN c.brand b " +
            "JOIN b.owner o " +
            "WHERE o.id = :ownerId")
    List<Reservation> findAllByOwnerId(@Param("ownerId") Long ownerId);
    List<Reservation> findByStatusAndMatchDateBefore(ReservationStatus status, LocalDateTime date);

    @Query("SELECT b.owner FROM Reservation r " +
            "JOIN r.cancha c " +
            "JOIN c.brand b " +
            "WHERE c.id = :canchaId")
    Owner findOwnerByCanchaId(@Param("canchaId") Long canchaId);

    /// money stats

    /// total

    @Query("""
    SELECT r.cancha.id, r.cancha.name, SUM(r.deposit)
    FROM Reservation r
    WHERE r.cancha.brand.owner.id = :ownerId
    GROUP BY r.cancha.id, r.cancha.name
    """)
    List<Object[]> getLifetimeCanchaEarnings(@Param("ownerId") Long ownerId);

    @Query("""
    SELECT r.cancha.brand.id, r.cancha.brand.brandName, SUM(r.deposit)
    FROM Reservation r
    WHERE r.cancha.brand.owner.id = :ownerId
    GROUP BY r.cancha.brand.id, r.cancha.brand.brandName
    """)
    List<Object[]> getLifetimeBrandEarnings(@Param("ownerId") Long ownerId);

    /// time-frame

    @Query("""
        SELECT r.cancha.id, r.cancha.name, FUNCTION('DATE', r.matchDate), SUM(r.deposit)
        FROM Reservation r
        WHERE r.cancha.brand.owner.id = :ownerId
          AND r.matchDate BETWEEN :start AND :end
        GROUP BY r.cancha.id, r.cancha.name, FUNCTION('DATE', r.matchDate)
        ORDER BY FUNCTION('DATE', r.matchDate) DESC
    """)
    List<Object[]> findCanchaEarnings(@Param("ownerId") Long ownerId,
                                      @Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);
    @Query("""
        SELECT r.cancha.brand.id, r.cancha.brand.brandName, FUNCTION('DATE', r.matchDate), SUM(r.deposit)
        FROM Reservation r
        WHERE r.cancha.brand.owner.id = :ownerId
          AND r.matchDate BETWEEN :start AND :end
        GROUP BY r.cancha.brand.id, r.cancha.brand.brandName, FUNCTION('DATE', r.matchDate)
        ORDER BY FUNCTION('DATE', r.matchDate) DESC
    """)
    List<Object[]> findBrandEarnings(@Param("ownerId") Long ownerId,
                                     @Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);
    // totales
    @Query("""
    SELECT cb.id AS brandId, cb.brandName, SUM(r.deposit) AS totalEarnings
    FROM Reservation r
    JOIN r.cancha c
    JOIN c.brand cb
    GROUP BY cb.id, cb.brandName
    """)
    List<Object[]> getAllLifetimeBrandEarnings();

    @Query("""
    SELECT c.id, c.name, SUM(r.deposit)
    FROM Reservation r
    JOIN r.cancha c
    GROUP BY c.id, c.name
    """)
    List<Object[]> getAllLifetimeCanchaEarnings();

}
