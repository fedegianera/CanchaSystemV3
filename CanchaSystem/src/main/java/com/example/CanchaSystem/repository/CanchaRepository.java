package com.example.CanchaSystem.repository;

import com.example.CanchaSystem.model.Cancha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CanchaRepository extends JpaRepository<Cancha,Long> {
    boolean existsById(Long id);
    boolean existsByAddress(String address);
    boolean existsByName(String name);
    boolean existsByIdAndBrandOwnerUsername(Long id, String username);
    Optional<Cancha> findById(Long id);

    List<Cancha> findByBrandOwnerUsername(String username);
    List<Cancha> findByBrandOwnerIdAndActive(Long id,boolean active);
    List<Cancha> findByBrandOwnerId(Long id);
    List<Cancha> findByBrandIdAndActiveAndWorking(Long id,boolean active, boolean working);
    List<Cancha> findByBrandId(Long id);
    List<Cancha> findByActiveAndWorking(boolean active, boolean working);

    Optional<Cancha> findByIdAndBrandOwnerUsernameAndActive(Long id, String username, boolean active);
}
