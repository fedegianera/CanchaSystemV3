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

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private RoleRepository roleRepository;

    public Admin insertAdmin(Admin admin) throws UsernameAlreadyExistsException {
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ADMIN")));

        if (!adminRepository.existsByUsername(admin.getUsername())) {
            admin.setRole(adminRole);
            return adminRepository.save(admin);
        }
        else throw new UsernameAlreadyExistsException("El nombre de usuario ya existe");
        }

    public List<Admin> getAllAdmins() throws NoAdminsException {
        List<Admin> admins = adminRepository.findAll();
        if(admins.isEmpty())
            throw new NoAdminsException("Todavia no hay administradores registrados");
        return admins;
    }

    public Admin updateAdmin(Admin admin) throws AdminNotFoundException {
        if(adminRepository.existsById(admin.getId())){
            return adminRepository.save(admin);
        }else
            throw new AdminNotFoundException("Administrador no encontrado");
    }

    public void deleteAdmin(Long id) throws AdminNotFoundException{
        if (adminRepository.existsById(id)) {
            adminRepository.deleteById(id);
        }else
            throw new AdminNotFoundException("Administrador no encontrado");

    }

    public Admin findAdminById(Long id) throws AdminNotFoundException {
        return adminRepository.findById(id).orElseThrow(()-> new AdminNotFoundException("Administrador no encontrado"));
    }
}
