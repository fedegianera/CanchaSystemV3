package com.example.CanchaSystem.repository;

import com.example.CanchaSystem.model.OwnerRequest;
import com.example.CanchaSystem.model.OwnerRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OwnerRequestRepository extends JpaRepository<OwnerRequest, Long> {
    boolean existsByClientIdAndStatus(Long clientId, OwnerRequestStatus status);
    Optional<OwnerRequest> findById(Long id);
    List<OwnerRequest> findByStatusAndActive(OwnerRequestStatus status, boolean active);

    Optional<OwnerRequest> findByClientId(Long clientId);
}
