package com.example.CanchaSystem.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String redirigirPorRol(Authentication auth) {
        if (auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/home-admin.html";
        } else if (auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_OWNER"))) {
            return "redirect:/home-owner.html";
        } else {
            return "redirect:/home-client.html";
        }
    }
}
