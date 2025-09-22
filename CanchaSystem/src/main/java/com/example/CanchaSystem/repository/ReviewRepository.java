package com.example.CanchaSystem.repository;

import com.example.CanchaSystem.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {
    boolean existsByCanchaIdAndClientIdAndActive(Long canchaId, Long clientId, boolean active);
    Optional<Review> findById(Long reviewId);

    List<Review> findByCanchaIdAndActive(Long canchaId,boolean active);
    List<Review> findByCanchaId(Long canchaId);
    List<Review> findByClientIdAndActive(Long clientId,boolean active);
}
