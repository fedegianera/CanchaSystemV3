package com.example.CanchaSystem.repository;

import com.example.CanchaSystem.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, UUID> {
    boolean existsById(UUID id);
    boolean existsByUsernameAndActive(String username, boolean active);
    boolean existsByMail(String mail);
    boolean existsByCellNumber(String cellNumber);

    Optional<Owner> findByUsernameAndActive(String username,boolean active);

}
