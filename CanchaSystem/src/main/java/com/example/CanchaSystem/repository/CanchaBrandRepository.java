package com.example.CanchaSystem.repository;

import com.example.CanchaSystem.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CanchaBrandRepository extends JpaRepository<Brand, Long> {
    boolean existsByBrandNameAndActive(String name, boolean active);
    List<Brand> findByOwnerIdAndActive(UUID ownerId, boolean active);
    List<Brand> findAllByActive(boolean active);
    Optional<Brand> findByIdAndActive(Long id, boolean active);

}
