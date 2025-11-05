package com.example.CanchaSystem.service;

import com.example.CanchaSystem.exception.client.ClientNotFoundException;
import com.example.CanchaSystem.exception.misc.IllegalAmountException;
import com.example.CanchaSystem.exception.misc.UnableToDropException;
import com.example.CanchaSystem.exception.misc.UsernameAlreadyExistsException;
import com.example.CanchaSystem.exception.owner.NoOwnersException;
import com.example.CanchaSystem.exception.owner.OwnerNotFoundException;
import com.example.CanchaSystem.exception.owner.UnactiveOwnerException;
import com.example.CanchaSystem.model.*;
import com.example.CanchaSystem.repository.AdminRepository;
import com.example.CanchaSystem.repository.ClientRepository;
import com.example.CanchaSystem.repository.OwnerRepository;
import com.example.CanchaSystem.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OwnerService {

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CanchaBrandService canchaBrandService;

    public Owner insertOwner(Owner owner) throws UsernameAlreadyExistsException {
        Role ownerRole = roleRepository.findByName("OWNER")
                .orElseGet(() -> roleRepository.save(new Role("OWNER")));

        if (ownerRepository.existsByUsernameAndActive(owner.getUsername(), true))
            throw new UsernameAlreadyExistsException("El nombre de usuario ya existe");

        owner.setRole(ownerRole);
        owner.setPassword(passwordEncoder.encode(owner.getPassword()));

        return ownerRepository.save(owner);
    }

    public List<Owner> getAllOwners() throws NoOwnersException {
        return ownerRepository.findAll();
    }

    public Owner updateOwner(Owner update) throws OwnerNotFoundException {
        Owner owner = findOwnerById(update.getId());

        owner.setName(update.getName());
        owner.setLastName(update.getLastName());
        owner.setUsername(update.getUsername());
        owner.setMail(update.getMail());
        owner.setCellNumber(update.getCellNumber());
        owner.setBankOwner(update.getBankOwner());

        return ownerRepository.save(owner);
    }

    public Owner addMoneyToOwnerBank(UUID ownerId, double addedAmount){
        if (addedAmount <= 0)
            throw new IllegalAmountException("Monto inválido");

        Owner owner = findOwnerById(ownerId);

        if (!owner.isActive())
            throw new UnactiveOwnerException("Dueño dado de baja");

        owner.setBankOwner(owner.getBankOwner()+addedAmount);
        return ownerRepository.save(owner);
    }

    public Owner updateOwnerAdmin(Owner ownerFromRequest) throws OwnerNotFoundException {
        Owner owner = findOwnerById(ownerFromRequest.getId());

        owner.setName(ownerFromRequest.getName());
        owner.setLastName(ownerFromRequest.getLastName());
        owner.setUsername(ownerFromRequest.getUsername());
        owner.setMail(ownerFromRequest.getMail());
        owner.setCellNumber(ownerFromRequest.getCellNumber());
        owner.setBankOwner(ownerFromRequest.getBankOwner());

        String pass = ownerFromRequest.getPassword();

        if (!pass.isEmpty()) {
            owner.setPassword(passwordEncoder.encode(pass));
        }

        return ownerRepository.save(owner);
    }

    public void deleteOwner(UUID ownerId){
        Owner owner = findOwnerById(ownerId);

        if (!owner.isActive())
            throw new UnableToDropException("El dueño ya está inactivo");

        List<Brand> brands = canchaBrandService.findCanchaBrandsByOwnerUsername(owner.getUsername());
        for (Brand brand : brands) {
            canchaBrandService.deleteCanchaBrand(brand.getId());
        }

        owner.setActive(false);
        ownerRepository.save(owner);
    }

    public Owner findOwnerById(UUID id) throws OwnerNotFoundException {
        return ownerRepository.findById(id)
                .orElseThrow(OwnerNotFoundException::new);
    }

    public boolean verifyUsername(String username) {
        return clientRepository.existsByUsernameAndActive(username, true)
                || adminRepository.existsByUsername(username)
                || ownerRepository.existsByUsernameAndActive(username, true);
    }
}

