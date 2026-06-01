package com.example.CanchaSystem.model;

import java.util.Optional;

public enum Role {
    ADMIN,
    CLIENT,
    OWNER;

    @Override
    public String toString() {
        return "ROLE_" + this.name();
    }

    public static Optional<Role> getRole(String role) {
        try {
            return Optional.of(Role.valueOf(role.replace("ROLE_", "")));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
