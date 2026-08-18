package com.example.CanchaSystem.repository;

import com.example.CanchaSystem.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {
    boolean existsByEstablishmentIdAndClientIdAndActive(Long establishmentId, UUID clientId, boolean active);
    Optional<Review> findByIdAndActive(Long reviewId, boolean active);

    List<Review> findByEstablishmentIdAndActive(Long establishmentId,boolean active);
    List<Review> findByEstablishmentId(Long establishmentId);
    List<Review> findByClientIdAndActive(UUID clientId, boolean active);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.establishment.id = :estId AND r.active = true")
    Double getAverageRatingByEstablishment(@Param("estId") Long establishmentId);

    @Query("""
    SELECT r.establishment.id, AVG(r.rating)
    FROM Review r
    WHERE r.active = true
    GROUP BY r.establishment.id
    """)
    List<Object[]> getAllEstablishmentAverages();
}
