package com.example.CanchaSystem.repository;

import com.example.CanchaSystem.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {
    boolean existsById(Long id);
    boolean existsByUsernameAndActive(String username, boolean active);
    boolean existsByMail(String mail);
    boolean existsByCellNumber(String cellNumber);

    Optional<Owner> findByUsernameAndActive(String username,boolean active);

    @Query("SELECT cb.owner FROM Cancha c JOIN c.brand cb WHERE c.id = :canchaId AND cb.owner.active = :active")
    Optional<Owner> findOwnerByCanchaIdAndActive(@Param("canchaId") Long canchaId, boolean active);

}
