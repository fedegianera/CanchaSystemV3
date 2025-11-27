package com.example.CanchaSystem.service;

import com.example.CanchaSystem.Mapper.OwnerMapper;
import com.example.CanchaSystem.dto.request.OwnerRequestDTO;
import com.example.CanchaSystem.dto.response.OwnerResponseDTO;
import com.example.CanchaSystem.exception.misc.*;
import com.example.CanchaSystem.exception.owner.NoOwnersException;
import com.example.CanchaSystem.exception.owner.OwnerNotFoundException;
import com.example.CanchaSystem.model.*;
import com.example.CanchaSystem.repository.*;
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
    private CanchaBrandRepository brandRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CanchaBrandService canchaBrandService;

    @Autowired
    private OwnerMapper ownerMapper;

    public OwnerResponseDTO insertOwner(OwnerRequestDTO ownerDto) throws UsernameAlreadyExistsException {
        if (clientRepository.existsByUsernameAndActive(ownerDto.username(), true) || adminRepository.existsByUsername(ownerDto.username()) || ownerRepository.existsByUsernameAndActive(ownerDto.username(), true)) {
            throw new UsernameAlreadyExistsException("El nombre de usuario ya existe");
        }

        if (clientRepository.existsByMailAndActive(ownerDto.mail(), true) || ownerRepository.existsByMailAndActive(ownerDto.mail(), true)) {
            throw new MailAlreadyRegisteredException("El mail ya esta registrado");
        }

        if (clientRepository.existsByCellNumberAndActive(ownerDto.cellNumber(), true) || ownerRepository.existsByCellNumberAndActive(ownerDto.cellNumber(), true)) {
            throw new CellNumberAlreadyAddedException("El numero de telefono ya esta registrado");
        }

        Role role = roleRepository.findByName("OWNER")
                .orElseGet(() -> roleRepository.save(new Role("OWNER")));

        Owner owner = Owner.builder()
                .name(ownerDto.name())
                .lastName(ownerDto.lastName())
                .username(ownerDto.username())
                .password(passwordEncoder.encode(ownerDto.password()))
                .mail(ownerDto.mail())
                .cellNumber(ownerDto.cellNumber())
                .active(true)
                .role(role)
                .build();

        ownerRepository.save(owner);

        return ownerMapper.toDto(owner);
    }

    public List<OwnerResponseDTO> getAllOwners() throws NoOwnersException {
        List<Owner> owners = ownerRepository.findAllByActive(true);
        return ownerMapper.toDto(owners);
    }

    public OwnerResponseDTO updateOwner(UUID id, OwnerRequestDTO ownerRequestDTO) throws OwnerNotFoundException {
        Optional<Owner> ownerOpt = ownerRepository.findByIdAndActive(id, true);


        if (ownerOpt.isEmpty()) {
            throw new OwnerNotFoundException("No se encontro el dueño");
        }

        Owner owner = ownerOpt.get();
        if ((clientRepository.existsByUsernameAndActive(ownerRequestDTO.username(), true) ||
                adminRepository.existsByUsername(ownerRequestDTO.username()) ||
                ownerRepository.existsByUsernameAndActive(ownerRequestDTO.username(), true)) &&
                !ownerRequestDTO.username().equals(owner.getUsername())) {
            throw new UsernameAlreadyExistsException("El nombre de usuario ya existe");
        }

        if ((clientRepository.existsByMailAndActive(ownerRequestDTO.mail(), true) ||
                ownerRepository.existsByMailAndActive(ownerRequestDTO.mail(), true)) &&
                !ownerRequestDTO.mail().equals(owner.getMail())) {
            throw new MailAlreadyRegisteredException("El mail ya esta registrado");
        }

        if ((clientRepository.existsByCellNumberAndActive(ownerRequestDTO.cellNumber(), true) ||
                ownerRepository.existsByCellNumberAndActive(ownerRequestDTO.cellNumber(), true)) &&
                !ownerRequestDTO.cellNumber().equals(owner.getCellNumber())) {
            throw new CellNumberAlreadyAddedException("El numero de telefono ya esta registrado");
        }

        owner.setName(ownerRequestDTO.name());
        owner.setLastName(ownerRequestDTO.lastName());
        owner.setUsername(ownerRequestDTO.username());
        owner.setMail(ownerRequestDTO.mail());
        owner.setCellNumber(ownerRequestDTO.cellNumber());

        ownerRepository.save(owner);

        return ownerMapper.toDto(owner);
    }

    public OwnerResponseDTO updateOwnerAdmin(UUID id, OwnerRequestDTO ownerRequestDTO) throws OwnerNotFoundException {
        Owner owner = ownerRepository.findByIdAndActive(id, true)
                .orElseThrow(() -> new OwnerNotFoundException("Dueño no encontrado"));

        if ((clientRepository.existsByUsernameAndActive(ownerRequestDTO.username(), true) ||
                adminRepository.existsByUsername(ownerRequestDTO.username()) ||
                ownerRepository.existsByUsernameAndActive(ownerRequestDTO.username(), true)) &&
                !ownerRequestDTO.username().equals(owner.getUsername())) {
            throw new UsernameAlreadyExistsException("El nombre de usuario ya existe");
        }

        if ((clientRepository.existsByMailAndActive(ownerRequestDTO.mail(), true) ||
                ownerRepository.existsByMailAndActive(ownerRequestDTO.mail(), true)) &&
                !ownerRequestDTO.mail().equals(owner.getMail())) {
            throw new MailAlreadyRegisteredException("El mail ya esta registrado");
        }

        if ((clientRepository.existsByCellNumberAndActive(ownerRequestDTO.cellNumber(), true) ||
                ownerRepository.existsByCellNumberAndActive(ownerRequestDTO.cellNumber(), true)) &&
                !ownerRequestDTO.cellNumber().equals(owner.getCellNumber())) {
            throw new CellNumberAlreadyAddedException("El numero de telefono ya esta registrado");
        }

        owner.setName(ownerRequestDTO.name());
        owner.setLastName(ownerRequestDTO.lastName());
        owner.setUsername(ownerRequestDTO.username());
        owner.setMail(ownerRequestDTO.mail());
        owner.setCellNumber(ownerRequestDTO.cellNumber());
        owner.setActive(ownerRequestDTO.active());

        String pass = ownerRequestDTO.password();

        if (!pass.isEmpty()) {
            owner.setPassword(passwordEncoder.encode(pass));
        }

        ownerRepository.save(owner);

        return ownerMapper.toDto(owner);
    }

    public void deleteOwner(UUID ownerId){

        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new OwnerNotFoundException("Owner no encontrado"));

        if (!owner.isActive())
            throw new UnableToDropException("El dueño ya esta inactiva");

        List<Brand> brands = brandRepository.findByOwnerIdAndActive(ownerId, true);

        for (Brand brand : brands) {
            canchaBrandService.deleteCanchaBrand(brand.getId());
        }

        owner.setActive(false);
        ownerRepository.save(owner);

    }

    public OwnerResponseDTO findOwnerById(UUID id) throws OwnerNotFoundException {
        Optional<Owner> ownerOpt = ownerRepository.findByIdAndActive(id, true);

        if (ownerOpt.isEmpty()) {
            throw new OwnerNotFoundException("Dueño no encontrado");
        }

        Owner owner = ownerOpt.get();

        return ownerMapper.toDto(owner);
    }

    public boolean verifyUsername(String username) {
        return clientRepository.existsByUsernameAndActive(username, true) || adminRepository.existsByUsername(username) || ownerRepository.existsByUsernameAndActive(username, true);
    }

}

