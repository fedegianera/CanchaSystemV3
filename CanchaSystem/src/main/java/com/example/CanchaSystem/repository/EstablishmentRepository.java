package com.example.CanchaSystem.repository;

import com.example.CanchaSystem.dto.response.CanchaResponseDTO;
import com.example.CanchaSystem.dto.response.EstablishmentResponseDTO;
import com.example.CanchaSystem.model.Establishment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstablishmentRepository  extends JpaRepository<EstablishmentResponseDTO, Long> {
    List<EstablishmentResponseDTO> findByActive(boolean active);

    List<EstablishmentResponseDTO> findByBrandId(Long id);
}
