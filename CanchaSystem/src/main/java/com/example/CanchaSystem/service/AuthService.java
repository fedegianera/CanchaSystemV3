package com.example.CanchaSystem.service;

import com.example.CanchaSystem.model.Admin;
import com.example.CanchaSystem.model.Client;
import com.example.CanchaSystem.model.Owner;
import com.example.CanchaSystem.repository.AdminRepository;
import com.example.CanchaSystem.repository.ClientRepository;
import com.example.CanchaSystem.repository.OwnerRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService implements UserDetailsService {

    private final ClientRepository clientRepository;
    private final OwnerRepository ownerRepository;
    private final AdminRepository adminRepository;

    public AuthService(ClientRepository clientRepository, OwnerRepository ownerRepository, AdminRepository adminRepository) {
        this.clientRepository = clientRepository;
        this.ownerRepository = ownerRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Intentando loguear con username: " + username);

        Optional<Client> clientOpt = clientRepository.findByUsernameAndActive(username, true);
        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            return new org.springframework.security.core.userdetails.User(
                    client.getUsername(),
                    client.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + client.getRole().getName()))
            );
        }

        Optional<Owner> ownerOpt = ownerRepository.findByUsernameAndActive(username, true);
        if (ownerOpt.isPresent()) {
            Owner owner = ownerOpt.get();
            System.out.println("Usuario encontrado como OWNER");
            System.out.println("Rol cargado para " + username + ": " + owner.getRole().getName());
            return new org.springframework.security.core.userdetails.User(
                    owner.getUsername(),
                    owner.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + owner.getRole().getName()))
            );
        }

        Optional<Admin> adminOpt = adminRepository.findByUsername(username);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            System.out.println("Usuario encontrado como ADMIN");
            return new org.springframework.security.core.userdetails.User(
                    admin.getUsername(),
                    admin.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + admin.getRole().getName()))
            );
        }
        throw new UsernameNotFoundException("Usuario no encontrado: " + username);
    }
}
