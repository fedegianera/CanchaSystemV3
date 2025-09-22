package com.example.CanchaSystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "cancha_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Cancha cancha;

    @Column(nullable = false)
    @DecimalMin(value = "1.0", message = "La valoración mínima es 1")
    @DecimalMax(value = "5.0", message = "La valoración máxima es 5")
    private double rating;

    @Column()
    @Size(
            max = 500,
            message = "Message only accepts caracters between 5 and 500"
    )
    private String message;

    @Column(nullable = false)
    private boolean active = true;
}
