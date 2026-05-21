package com.example.CanchaSystem.service;

import com.example.CanchaSystem.Mapper.OwnerMapper;
import com.example.CanchaSystem.dto.request.OwnerRequestDTO;
import com.example.CanchaSystem.dto.response.OwnerResponseDTO;
import com.example.CanchaSystem.exception.misc.*;
import com.example.CanchaSystem.exception.owner.NoOwnersException;
import com.example.CanchaSystem.exception.user.UserNotFoundException;
import com.example.CanchaSystem.model.*;
import com.example.CanchaSystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class OwnerService {

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CanchaBrandRepository brandRepository;

    @Autowired
    private CanchaBrandService canchaBrandService;

    @Autowired
    private OwnerMapper ownerMapper;

    @Autowired
    private UserService userService;

    public OwnerResponseDTO insertOwner(OwnerRequestDTO ownerDto) throws UsernameAlreadyExistsException {
        userService.verifyNonExistenceOrThrow(
                ownerDto.username(),
                ownerDto.mail(),
                ownerDto.cellNumber()
        );

        Owner owner = Owner.builder()
                .name(ownerDto.name())
                .lastName(ownerDto.lastName())
                .username(ownerDto.username())
                .password(passwordEncoder.encode(ownerDto.password()))
                .mail(ownerDto.mail())
                .cellNumber(ownerDto.cellNumber())
                .active(true)
                .build();

        ownerRepository.save(owner);

        return ownerMapper.toDto(owner);
    }

    public Owner findOwnerOrThrow(UUID id) {
        return ownerRepository.findByIdAndActive(id, true)
                .orElseThrow(() -> new UserNotFoundException(id, Role.OWNER));
    }

    public Owner findOwnerOrThrow(String username) {
        return ownerRepository.findByUsernameAndActive(username, true)
                .orElseThrow(() -> new UserNotFoundException(username, Role.OWNER));
    }

    public List<OwnerResponseDTO> getAllOwners() throws NoOwnersException {
        List<Owner> owners = ownerRepository.findAllByActive(true);
        return ownerMapper.toDto(owners);
    }

    public OwnerResponseDTO updateOwner(UUID id, OwnerRequestDTO ownerRequestDTO) throws UserNotFoundException {
        Owner owner = findOwnerOrThrow(id);
        modifyOwner(owner, ownerRequestDTO);

        ownerRepository.save(owner);
        return ownerMapper.toDto(owner);
    }

    public OwnerResponseDTO updateOwnerAdmin(UUID id, OwnerRequestDTO ownerRequestDTO) throws UserNotFoundException {
        Owner owner = findOwnerOrThrow(id);
        modifyOwner(owner, ownerRequestDTO);

        String pass = ownerRequestDTO.password();
        if (!pass.isEmpty()) {
            owner.setPassword(passwordEncoder.encode(pass));
        }

        ownerRepository.save(owner);

        return ownerMapper.toDto(owner);
    }

    private void modifyOwner(Owner owner, OwnerRequestDTO ownerDto) {
        userService.verifyNonExistenceOrThrow(
                ownerDto.username(),
                ownerDto.mail(),
                ownerDto.cellNumber(),
                owner.getUsername(),
                owner.getMail(),
                owner.getCellNumber()
        );

        owner.setName(ownerDto.name());
        owner.setLastName(ownerDto.lastName());
        owner.setUsername(ownerDto.username());
        owner.setMail(ownerDto.mail());
        owner.setCellNumber(ownerDto.cellNumber());
    }

    public Owner deleteOwner(UUID ownerId) throws UserNotFoundException {
        Owner owner = findOwnerOrThrow(ownerId);

        brandRepository.findByOwnerIdAndActive(ownerId, true).forEach(brand ->
                canchaBrandService.deleteCanchaBrand(brand.getId()));

        owner.setActive(false);
        return ownerRepository.save(owner);
    }

    public OwnerResponseDTO findOwnerById(UUID id) throws UserNotFoundException {
        return ownerMapper.toDto(
                findOwnerOrThrow(id)
        );
    }
}

