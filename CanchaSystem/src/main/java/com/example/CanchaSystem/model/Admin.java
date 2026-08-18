package com.example.CanchaSystem.model;

import com.example.CanchaSystem.interfaces.IUser;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Admin implements IUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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

    @Override
    public String getRoleName() {
        return Role.ADMIN.toString();
    }
}
