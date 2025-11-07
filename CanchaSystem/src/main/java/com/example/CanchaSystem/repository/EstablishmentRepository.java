package com.example.CanchaSystem.repository;

import com.example.CanchaSystem.dto.response.CanchaResponseDTO;
import com.example.CanchaSystem.dto.response.EstablishmentResponseDTO;
import com.example.CanchaSystem.model.Establishment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstablishmentRepository  extends JpaRepository<Establishment, Long> {
    List<EstablishmentResponseDTO> findByActive(boolean active);

    List<EstablishmentResponseDTO> findByBrandId(Long id);

    Optional<EstablishmentResponseDTO> findByIdAndActive(Long id, boolean active);

    boolean existsByName(String name);


}
