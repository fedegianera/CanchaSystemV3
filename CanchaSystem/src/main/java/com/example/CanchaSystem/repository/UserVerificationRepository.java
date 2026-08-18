package com.example.CanchaSystem.repository;

import com.example.CanchaSystem.model.UserVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserVerificationRepository extends JpaRepository<UserVerification, UUID> {
    @Query("""
SELECT c
FROM UserVerification c
WHERE
c.expiryTime < :now
OR
c.client.active = FALSE
""")
    List<UserVerification> findExpired(@Param("now") Long currentTime);
}
