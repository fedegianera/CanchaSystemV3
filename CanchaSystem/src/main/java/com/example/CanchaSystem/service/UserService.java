package com.example.CanchaSystem.service;

import com.example.CanchaSystem.exception.misc.CellNumberAlreadyAddedException;
import com.example.CanchaSystem.exception.misc.MailAlreadyRegisteredException;
import com.example.CanchaSystem.exception.misc.UsernameAlreadyExistsException;
import com.example.CanchaSystem.repository.AdminRepository;
import com.example.CanchaSystem.repository.ClientRepository;
import com.example.CanchaSystem.repository.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService {
    private final AdminRepository adminRepository;
    private final ClientRepository clientRepository;
    private final OwnerRepository ownerRepository;

    @Autowired
    public UserService(AdminRepository adminRepository, ClientRepository clientRepository, OwnerRepository ownerRepository) {
        this.adminRepository = adminRepository;
        this.clientRepository = clientRepository;
        this.ownerRepository = ownerRepository;
    }

    public void verifyNonExistenceOrThrow(String username, String mail, String phone, String existingUsername, String existingMail, String existingPhone) {
        if (!Objects.equals(username, existingUsername) && existsByUsername(username))
            throw new UsernameAlreadyExistsException("El nombre de usuario ya está registrado");
        if (!Objects.equals(mail, existingMail) && existsByMail(mail))
            throw new MailAlreadyRegisteredException("El correo ya está registrado");
        if (!Objects.equals(phone, existingPhone) && existsByPhoneNumber(phone))
            throw new CellNumberAlreadyAddedException("El número de teléfono ya está registrado");
    }

    public void verifyNonExistenceOrThrow(String username, String mail, String phone) throws UsernameAlreadyExistsException, MailAlreadyRegisteredException, CellNumberAlreadyAddedException {
        verifyNonExistenceOrThrow(username, mail, phone, null, null, null);
    }

    public boolean existsByUsername(String username) {
        return clientRepository.existsByUsernameAndActive(username, true)
                || ownerRepository.existsByUsernameAndActive(username, true)
                || adminRepository.existsByUsername(username);
    }

    public boolean existsByMail(String mail) {
        return clientRepository.existsByMailAndActive(mail, true)
                || ownerRepository.existsByMailAndActive(mail, true);
    }

    public boolean existsByPhoneNumber(String phone) {
        return clientRepository.existsByCellNumberAndActive(phone, true)
                || ownerRepository.existsByCellNumberAndActive(phone, true);
    }
}
