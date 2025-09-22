package com.example.CanchaSystem.repository;

import com.example.CanchaSystem.model.CanchaBrand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CanchaBrandRepository extends JpaRepository<CanchaBrand, Long> {
    boolean existsByBrandName(String name);
    List<CanchaBrand> findByOwnerIdAndActive(Long ownerId,boolean active);

}
