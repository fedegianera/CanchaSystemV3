package com.example.CanchaSystem.config;

import com.example.CanchaSystem.model.Admin;
import com.example.CanchaSystem.model.Client;
import com.example.CanchaSystem.model.Owner;
import com.example.CanchaSystem.model.Role;
import com.example.CanchaSystem.repository.AdminRepository;
import com.example.CanchaSystem.repository.ClientRepository;
import com.example.CanchaSystem.repository.OwnerRepository;
import com.example.CanchaSystem.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            RoleRepository roleRepo,
            ClientRepository clientRepo,
            OwnerRepository ownerRepo,
            AdminRepository adminRepo,
            PasswordEncoder encoder
    ) {
        return args -> {
            Role adminRole = roleRepo.findByName("ADMIN")
                    .orElseGet(() -> roleRepo.save(new Role("ADMIN")));
            Role clientRole = roleRepo.findByName("CLIENT")
                    .orElseGet(() -> roleRepo.save(new Role("CLIENT")));
            Role ownerRole = roleRepo.findByName("OWNER")
                    .orElseGet(() -> roleRepo.save(new Role("OWNER")));
        };
    }
}
