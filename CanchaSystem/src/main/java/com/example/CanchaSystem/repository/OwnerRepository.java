package com.example.CanchaSystem.repository;

import com.example.CanchaSystem.model.Admin;
import com.example.CanchaSystem.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, UUID> {
    boolean existsById(UUID id);
    boolean existsByUsernameAndActive(String username, boolean active);
    boolean existsByMailAndActive(String mail, boolean active);
    boolean existsByCellNumberAndActive(String cellNumber, boolean active);

    Optional<Owner> findByUsernameAndActive(String username,boolean active);
    Optional<Owner> findByUsername(String username);

    List<Owner> findAllByActive(boolean active);
    Optional<Owner> findByIdAndActive(UUID id, boolean active);

}
