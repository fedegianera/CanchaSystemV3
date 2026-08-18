package com.example.CanchaSystem.repository;

import com.example.CanchaSystem.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
   List<Client> findAllByActive(boolean active);
   List<Client> findAllByActiveAndVerified(boolean active, boolean verified);
   boolean existsByIdAndActive(UUID id, boolean active);
   boolean existsByUsernameAndActive(String username, boolean active);
   boolean existsByMailAndActive(String mail, boolean active);
   boolean existsByCellNumberAndActive(String cellNumber, boolean active);
   long countByActive(boolean active);
   Optional<Client> findByUsernameAndActive(String username, boolean active);
   Optional<Client> findByUsernameAndActiveAndVerified(String username, boolean active, boolean verified);
   Optional<Client> findByUsername(String username);

   Optional<Client> findByIdAndActive(UUID id, boolean active);

}