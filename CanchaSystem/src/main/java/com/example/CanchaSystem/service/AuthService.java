package com.example.CanchaSystem.service;

import com.example.CanchaSystem.interfaces.IUser;
import com.example.CanchaSystem.repository.AdminRepository;
import com.example.CanchaSystem.repository.ClientRepository;
import com.example.CanchaSystem.repository.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private AdminRepository adminRepository;

    public AuthService(ClientRepository clientRepository, OwnerRepository ownerRepository, AdminRepository adminRepository) {
        this.clientRepository = clientRepository;
        this.ownerRepository = ownerRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return clientRepository.findByUsernameAndActive(username,true).map(this::createUserInstance)
                .or(() -> ownerRepository.findByUsernameAndActive(username,true).map(this::createUserInstance))
                .or(() -> adminRepository.findByUsername(username).map(this::createUserInstance))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario "+ username +" no encontrado"));
    }

    private UserDetails createUserInstance(IUser user){
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority(user.getRoleName()))
                .build();
    }
}
