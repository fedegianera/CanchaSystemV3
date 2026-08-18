package com.example.CanchaSystem.model;

import com.example.CanchaSystem.interfaces.IUser;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Owner implements IUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
//    @Size(
//            min = 2,
//            message = "The Name must have 2 caracters"
//    )
    private String name;

    @Column(nullable = false)
//    @Size(
//            min = 2,
//            message = "The Last Name must have 2 caracters"
//    )
    private String lastName;

    @Column(nullable = false, unique = true)
//    @Size(
//            min = 4,
//            message = "The Username must have 4 caracters"
//    )
    private String username;

    @Column(nullable = false)
//    @Size(
//            min = 4,
//            message = "The Password must have 4 caracters"
//    )
    private String password;

    @Column(unique = true)
//    @Email(message = "The email is not valid")
    private String mail;

    @Column(unique = true)
//    @Size(
//            min = 8,
//            max = 14,
//            message = "The cell number must have between 8 and 14 caracters"
//    )
    private String cellNumber;

    @Column(nullable = false)
    private boolean active = true;

    @Override
    public String getRoleName() {
        return Role.OWNER.toString();
    }
}
