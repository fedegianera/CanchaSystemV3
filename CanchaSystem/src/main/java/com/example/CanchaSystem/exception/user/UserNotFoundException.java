package com.example.CanchaSystem.exception.user;

import com.example.CanchaSystem.exception.ResourceNotFoundException;
import com.example.CanchaSystem.model.Role;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserNotFoundException extends ResourceNotFoundException {
    private final String userId;
    private final Role role;

    public UserNotFoundException(String userId, Role role, String username) {
        super(getStringRole(role) + " no encontrado. id= " + userId + ", username:" + username);

        this.userId = userId;
        this.role = role;
    }

    public UserNotFoundException(UUID userId, Role role) {
        this(userId.toString(), role, "?");
    }

    public UserNotFoundException(String username, Role role) {
        this("?", role, username);
    }

    private static String getStringRole(Role role) {
        return switch (role) {
            case ADMIN -> "Admin";
            case CLIENT -> "Cliente";
            case OWNER -> "Dueño";
        };
    }
}
