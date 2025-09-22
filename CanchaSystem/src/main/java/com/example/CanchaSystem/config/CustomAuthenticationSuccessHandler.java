package com.example.CanchaSystem.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();

            System.out.println(role);

            switch (role) {
                case "ROLE_ADMIN":
                    response.sendRedirect("/home-admin.html");
                    return;
                case "ROLE_OWNER":
                    response.sendRedirect("/home-owner.html");
                    return;
                case "ROLE_CLIENT":
                    response.sendRedirect("/home-client.html");
                    return;
            }
        }

        response.sendRedirect("/login.html?error=rol");
    }
}