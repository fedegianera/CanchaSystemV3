package com.example.CanchaSystem.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            response.sendRedirect(getRoleHomeUrl(authority.getAuthority()));
        }
        response.sendRedirect("/login.html?error=rol");
    }

    private static String getRoleHomeUrl(String role) {
        return switch (role) {
            case "ROLE_ADMIN" -> "/home-admin.html";
            case "ROLE_CLIENT" -> "/home-client.html";
            case "ROLE_OWNER" -> "/home-owner.html";
            default -> "/";
        };
    }
}