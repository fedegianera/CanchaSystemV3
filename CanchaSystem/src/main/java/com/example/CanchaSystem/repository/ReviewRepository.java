package com.example.CanchaSystem.repository;

import com.example.CanchaSystem.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {
    boolean existsByEstablishmentIdAndClientIdAndActive(Long establishmentId, Long clientId, boolean active);
    Optional<Review> findByIdAndActive(Long reviewId, boolean active);

    List<Review> findByEstablishmentIdAndActive(Long establishmentId,boolean active);
    List<Review> findByEstablishmentId(Long establishmentId);
    List<Review> findByClientIdAndActive(UUID clientId, boolean active);
}
