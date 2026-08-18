package com.example.CanchaSystem.repository;

import com.example.CanchaSystem.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminRepository extends JpaRepository<Admin,UUID>{

    boolean existsById(UUID id);

    boolean existsByUsername(String username);

    Optional<Admin> findByUsername(String username);

}
