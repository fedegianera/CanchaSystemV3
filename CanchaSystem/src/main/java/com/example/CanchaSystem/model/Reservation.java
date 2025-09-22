package com.example.CanchaSystem.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Date;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"matchDate", "cancha_id"})
)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id",nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "cancha_id",nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Cancha cancha;

    @Column(nullable = false)
    @PastOrPresent
    private LocalDateTime reservationDate;

    @Column(nullable = false)
    private LocalDateTime matchDate;

    @Column(nullable = false)
    @Min(1)
    private Double deposit;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.PENDING;

    @PrePersist
    public void prePersist() {
        if (reservationDate == null) {
            reservationDate = LocalDateTime.now();
        }
    }
}
