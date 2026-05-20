package com.example.CanchaSystem.exception.user;

import com.example.CanchaSystem.exception.ResourceNotFoundException;
import com.example.CanchaSystem.model.Role;
import lombok.Getter;

@Getter
public class UserNotFoundException extends ResourceNotFoundException {
    private final Long userId;
    private final Role role;

    public UserNotFoundException(Long userId, Role role) {
        super(getStringRole(role) + " no encontrado. ID: " + userId);

        this.userId = userId;
        this.role = role;
    }

    private static String getStringRole(Role role) {
        return switch (role) {
            case ADMIN -> "Admin";
            case CLIENT -> "Cliente";
            case OWNER -> "Dueño";
        };
    }
}
