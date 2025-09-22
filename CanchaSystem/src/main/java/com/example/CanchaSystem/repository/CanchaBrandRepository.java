package com.example.CanchaSystem.repository;

import com.example.CanchaSystem.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CanchaBrandRepository extends JpaRepository<Brand, Long> {
    boolean existsByBrandName(String name);
    List<Brand> findByOwnerIdAndActive(Long ownerId, boolean active);

}
