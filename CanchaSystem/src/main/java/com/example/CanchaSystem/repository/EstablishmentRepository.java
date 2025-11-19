package com.example.CanchaSystem.repository;

import com.example.CanchaSystem.dto.response.CanchaResponseDTO;
import com.example.CanchaSystem.dto.response.EstablishmentResponseDTO;
import com.example.CanchaSystem.model.Establishment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EstablishmentRepository  extends JpaRepository<Establishment, Long> {
    List<Establishment> findByActive(boolean active);

    List<Establishment> findByBrandIdAndActive(Long id, boolean active);

    Optional<Establishment> findByIdAndActive(Long id, boolean active);

    boolean existsByNameAndActive(String name, boolean active);

    List<Establishment> findByBrand_Owner_IdAndActive(UUID ownerId, boolean active);

}
