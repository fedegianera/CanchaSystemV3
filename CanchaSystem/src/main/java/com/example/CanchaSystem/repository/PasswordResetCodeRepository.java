
package com.example.CanchaSystem.repository;

import com.example.CanchaSystem.model.PasswordResetCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, Long> {

    Optional<PasswordResetCode> findTopByUsernameAndUsedOrderByIdDesc(String username, boolean used);

    Optional<PasswordResetCode> findTopByUsernameAndCodeAndUsedOrderByIdDesc(String username, String code, boolean used);
}