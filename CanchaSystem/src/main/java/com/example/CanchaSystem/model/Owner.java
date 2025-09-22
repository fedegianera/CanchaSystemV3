package com.example.CanchaSystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Owner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @Size(
            min = 2,
            message = "The Name must have 2 caracters"
    )
    private String name;

    @Column(nullable = false)
    @Size(
            min = 2,
            message = "The Last Name must have 2 caracters"
    )
    private String lastName;

    @Column(nullable = false,unique = true)
    @Size(
            min = 4,
            message = "The Username must have 4 caracters"
    )
    private String username;

    @Column(nullable = false)
    @Size(
            min = 4,
            message = "The Password must have 4 caracters"
    )
    private String password;

    @Column(nullable = true,unique = true)
    @Email(message = "The email is not valid")
    private String mail;

    @Column(nullable = true,unique = true)
    @Size(
            min = 8,
            max = 14,
            message = "The cell number must have between 8 and 14 caracters"
    )
    private String cellNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(nullable = false)
    private double bankOwner=0;

    @Column(nullable = false)
    private boolean active = true;
}
