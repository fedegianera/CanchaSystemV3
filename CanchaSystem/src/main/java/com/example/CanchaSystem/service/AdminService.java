package com.example.CanchaSystem.service;

import com.example.CanchaSystem.exception.misc.UsernameAlreadyExistsException;
import com.example.CanchaSystem.exception.user.UserNotFoundException;
import com.example.CanchaSystem.model.Admin;
import com.example.CanchaSystem.model.Role;
import com.example.CanchaSystem.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Admin insertAdmin(Admin admin) throws UsernameAlreadyExistsException {
        if (!adminRepository.existsByUsername(admin.getUsername())) {
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
            return adminRepository.save(admin);
        } else
            throw new UsernameAlreadyExistsException("El nombre de usuario ya existe");
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public Admin updateAdmin(Admin admin) throws UserNotFoundException {
        if(adminRepository.existsById(admin.getId())){
            return adminRepository.save(admin);
        }else
            throw new UserNotFoundException(admin.getId(), Role.ADMIN);
    }

    public void deleteAdmin(UUID id) throws UserNotFoundException{
        if (adminRepository.existsById(id)) {
            adminRepository.deleteById(id);
        }else
            throw new UserNotFoundException(id, Role.ADMIN);

    }

    public Admin findAdminById(UUID id) throws UserNotFoundException {
        return adminRepository.findById(id).orElseThrow(()-> new UserNotFoundException(id, Role.ADMIN));
    }
}
