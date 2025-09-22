package com.example.CanchaSystem.repository;

import com.example.CanchaSystem.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin,Long>{

    boolean existsById(Long id);

    boolean existsByUsername(String username);

    Optional<Admin> findByUsername(String username);

}
