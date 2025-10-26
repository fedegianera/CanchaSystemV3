package com.example.CanchaSystem.service;

import com.example.CanchaSystem.exception.misc.UsernameAlreadyExistsException;
import com.example.CanchaSystem.exception.admin.AdminNotFoundException;
import com.example.CanchaSystem.exception.admin.NoAdminsException;
import com.example.CanchaSystem.model.Admin;
import com.example.CanchaSystem.model.Role;
import com.example.CanchaSystem.repository.AdminRepository;
import com.example.CanchaSystem.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private RoleRepository roleRepository;

    public Admin insertAdmin(Admin admin) throws UsernameAlreadyExistsException {
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ADMIN")));

        if (adminRepository.existsByUsername(admin.getUsername()))
            throw new UsernameAlreadyExistsException("El nombre de usuario ya existe");

        admin.setRole(adminRole);
        return adminRepository.save(admin);
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public Admin updateAdmin(Admin updated) throws AdminNotFoundException {
        Admin admin = findAdminById(updated.getId());

        admin.setUsername(updated.getUsername());
        admin.setPassword(updated.getPassword());

        return adminRepository.save(admin);
    }

    public Admin deleteAdmin(UUID id) throws AdminNotFoundException {
        Optional<Admin> admin = adminRepository.findById(id);

        if (admin.isEmpty())
            throw new AdminNotFoundException("Administrador no encontrado");

        adminRepository.deleteById(id);
        return admin.get();
    }

    public Admin findAdminById(UUID id) throws AdminNotFoundException {
        return adminRepository.findById(id).orElseThrow(()-> new AdminNotFoundException("Administrador no encontrado"));
    }
}
