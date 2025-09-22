package com.example.CanchaSystem.service;

import com.example.CanchaSystem.exception.client.ClientNotFoundException;
import com.example.CanchaSystem.exception.misc.IllegalAmountException;
import com.example.CanchaSystem.exception.misc.UnableToDropException;
import com.example.CanchaSystem.exception.misc.UsernameAlreadyExistsException;
import com.example.CanchaSystem.exception.owner.NoOwnersException;
import com.example.CanchaSystem.exception.owner.OwnerNotFoundException;
import com.example.CanchaSystem.exception.owner.UnactiveOwnerException;
import com.example.CanchaSystem.exception.review.ReviewNotFoundException;
import com.example.CanchaSystem.model.*;
import com.example.CanchaSystem.repository.AdminRepository;
import com.example.CanchaSystem.repository.ClientRepository;
import com.example.CanchaSystem.repository.OwnerRepository;
import com.example.CanchaSystem.repository.RoleRepository;
import org.hibernate.annotations.OnDelete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

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

        if(!ownerRepository.existsByUsernameAndActive(owner.getUsername(), true)) {
            owner.setRole(ownerRole);

            owner.setPassword(passwordEncoder.encode(owner.getPassword()));
            return ownerRepository.save(owner);

        }  else throw new UsernameAlreadyExistsException("El nombre de usuario ya existe");
    }

    public List<Owner> getAllOwners() throws NoOwnersException {
        List<Owner> owners = ownerRepository.findAll();
        if(owners.isEmpty())
            throw new NoOwnersException("Todavia no hay dueños registrados");
        return owners;
    }

    public Owner updateOwner(Owner ownerFromRequest) throws OwnerNotFoundException {
        Owner owner = ownerRepository.findById(ownerFromRequest.getId())
                .orElseThrow(() -> new ClientNotFoundException("Dueño no encontrado"));

        owner.setName(ownerFromRequest.getName());
        owner.setLastName(ownerFromRequest.getLastName());
        owner.setUsername(ownerFromRequest.getUsername());
        owner.setMail(ownerFromRequest.getMail());
        owner.setCellNumber(ownerFromRequest.getCellNumber());
        owner.setBankOwner(ownerFromRequest.getBankOwner());

        return ownerRepository.save(owner);
    }

    public Owner addMoneyToOwnerBank(long ownerId,double addedAmount){

        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new OwnerNotFoundException("Dueño no encontrado"));

        if (!owner.isActive())
            throw new UnactiveOwnerException("Dueño dado de baja");

        if (addedAmount <= 0)
            throw new IllegalAmountException("Monto invalido");

        owner.setBankOwner(owner.getBankOwner()+addedAmount);
        return ownerRepository.save(owner);

    }

    public Owner updateOwnerAdmin(Owner ownerFromRequest) throws OwnerNotFoundException {
        Owner owner = ownerRepository.findById(ownerFromRequest.getId())
                .orElseThrow(() -> new OwnerNotFoundException("Dueño no encontrado"));

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

    public void deleteOwner(Long ownerId){

        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new OwnerNotFoundException("Owner no encontrado"));

        if (!owner.isActive())
            throw new UnableToDropException("El dueño ya esta inactiva");

        List<CanchaBrand> canchaBrands = canchaBrandService.findCanchaBrandsByOwnerUsername(owner.getUsername());

        for (CanchaBrand canchaBrand : canchaBrands) {
            canchaBrandService.deleteCanchaBrand(canchaBrand.getId());
        }

        owner.setActive(false);
        ownerRepository.save(owner);

    }

    public Owner findOwnerById(Long id) throws OwnerNotFoundException {
        return ownerRepository.findById(id).orElseThrow(()-> new OwnerNotFoundException("Dueño no encontrado"));
    }

    public Optional<Owner> getOwnerByCanchaId(Long canchaId) {
        return ownerRepository.findOwnerByCanchaIdAndActive(canchaId, true);
    }

    public boolean verifyUsername(String username) {
        return clientRepository.existsByUsernameAndActive(username, true) || adminRepository.existsByUsername(username) || ownerRepository.existsByUsernameAndActive(username, true);
    }

}

