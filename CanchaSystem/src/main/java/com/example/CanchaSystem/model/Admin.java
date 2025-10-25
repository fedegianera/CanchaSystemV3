package com.example.CanchaSystem.model;

import com.example.CanchaSystem.interfaces.IUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.lang.NonNullFields;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Admin implements IUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false,unique = true)
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Override
    public String getRoleName() {
        return role.getName();
    }
}
