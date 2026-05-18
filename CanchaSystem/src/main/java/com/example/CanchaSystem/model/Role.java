package com.example.CanchaSystem.model;

public enum Role {
    ADMIN,
    CLIENT,
    OWNER;

    @Override
    public String toString() {
        return "ROLE_" + this.name();
    }
}
