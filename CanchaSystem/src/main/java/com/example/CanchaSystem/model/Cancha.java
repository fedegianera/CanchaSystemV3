package com.example.CanchaSystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cancha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Min(1)
    private Double totalAmount;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean hasRoof;

    @ManyToOne
    @JoinColumn(name = "establishment_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Establishment establishment;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CanchaType canchaType;

    @Column(nullable = false)
    private boolean working;

    public int getTotalPlayers() {
        if (canchaType == null) return 0;
        return canchaType.getTotalPlayers();
    }

}
