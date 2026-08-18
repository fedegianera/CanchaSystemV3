package com.example.CanchaSystem.dto.request;

public record ResetPasswordDTO(String username, String code, String newPassword) {
}
