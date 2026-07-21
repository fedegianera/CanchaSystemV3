package com.example.CanchaSystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Establishment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Brand brand;

    @OneToOne
    @JoinColumn(name = "address_id", nullable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Address address;

    @Column(nullable = false)
    private LocalTime openingHour;

    @Column(nullable = false)
    private LocalTime closingHour;

    @Column(nullable = false)
    private boolean canShower;

    @Column(nullable = false)
    private boolean active = true;

}
