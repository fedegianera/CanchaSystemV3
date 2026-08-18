
package com.example.CanchaSystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_code")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @Column(nullable = false)
    private boolean used = false;

    @Column(nullable = false)
    private boolean verified = false;

    public PasswordResetCode(String username, String code, LocalDateTime expirationDate) {
        this.username = username;
        this.code = code;
        this.expirationDate = expirationDate;
        this.used = false;
        this.verified = false;
    }
}