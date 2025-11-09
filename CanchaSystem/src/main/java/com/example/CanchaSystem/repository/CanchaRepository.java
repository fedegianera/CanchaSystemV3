package com.example.CanchaSystem.repository;

import com.example.CanchaSystem.dto.response.CanchaResponseDTO;
import com.example.CanchaSystem.model.Cancha;
import com.example.CanchaSystem.model.CanchaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CanchaRepository extends JpaRepository<Cancha,Long> {
    boolean existsById(Long id);
    Optional<Cancha> findById(Long id);

    List<Cancha> findByEstablishmentIdAndActiveAndWorking(Long id,boolean active, boolean working);
    List<Cancha> findByEstablishmentId(Long id);
    List<Cancha> findByActiveAndWorking(boolean active, boolean working);

    @Query("SELECT DISTINCT c.canchaType FROM Cancha c WHERE c.establishment.id = :establishmentId")
    List<CanchaType> findDistinctTypesByEstablishmentId(@Param("establishmentId") Long establishmentId);

}
