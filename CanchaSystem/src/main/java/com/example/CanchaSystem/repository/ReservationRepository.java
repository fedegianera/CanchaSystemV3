package com.example.CanchaSystem.repository;

import com.example.CanchaSystem.dto.response.ReservationResponseDTO;
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
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    boolean existsById(Long id);
    boolean existsBymatchDateAndCanchaId(LocalDateTime date,Long canchaId);
    Optional<Reservation> findById(Long id);

    List<Reservation> findByCanchaId(Long canchaId);
    List<Reservation> findByClientId(UUID clientId);
    List<Reservation> findByCanchaIdAndMatchDateBetween(Long canchaId, LocalDateTime from, LocalDateTime until);
    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByMatchDateBetweenAndStatus(LocalDateTime from, LocalDateTime until,ReservationStatus status);
    List<Reservation> findByMatchDateBeforeAndStatus(LocalDateTime now, ReservationStatus status);
    List<Reservation> findByCanchaIdAndMatchDateBetweenAndStatus(Long canchaId, LocalDateTime start, LocalDateTime end, ReservationStatus status);



}
