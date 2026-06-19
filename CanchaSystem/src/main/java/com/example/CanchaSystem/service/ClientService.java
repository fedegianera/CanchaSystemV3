package com.example.CanchaSystem.service;

import com.example.CanchaSystem.Mapper.ClientMapper;
import com.example.CanchaSystem.Mapper.ReviewMapper;
import com.example.CanchaSystem.dto.request.ClientRequestDTO;
import com.example.CanchaSystem.dto.response.ClientResponseDTO;
import com.example.CanchaSystem.dto.response.OwnerResponseDTO;
import com.example.CanchaSystem.dto.response.ReviewResponseDTO;
import com.example.CanchaSystem.exception.misc.*;
import com.example.CanchaSystem.exception.review.ReviewNotFoundException;
import com.example.CanchaSystem.exception.user.UserNotFoundException;
import com.example.CanchaSystem.model.*;
import com.example.CanchaSystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private UserVerificationService userVerificationService;

    @Autowired
    private MailService mailService;

    @Autowired
    private OwnerService ownerService;


    public ClientResponseDTO insertClient(ClientRequestDTO clientDTO) {
        userService.verifyNonExistenceOrThrow(
                clientDTO.username(),
                clientDTO.mail(),
                clientDTO.cellNumber()
        );

        Client client = Client.builder()
                .name(clientDTO.name())
                .lastName(clientDTO.lastName())
                .username(clientDTO.username())
                .password(passwordEncoder.encode(clientDTO.password()))
                .mail(clientDTO.mail())
                .cellNumber(clientDTO.cellNumber())
                .active(true)
                .verified(false)
                .build();

        clientRepository.save(client);
        mailService.sendMail(
                client,
                userVerificationService.createUserVerification(client).getId()
        );

        return clientMapper.toDto(client);
    }

    public Client findClientOrThrow(UUID id) {
        return clientRepository.findByIdAndActive(id, true)
                .orElseThrow(() -> new UserNotFoundException(id, Role.CLIENT));
    }

    public Client findClientOrThrow(String username) {
        return clientRepository.findByUsernameAndActiveAndVerified(username, true, true)
                .orElseThrow(() -> new UserNotFoundException(username, Role.CLIENT));
    }

    public List<ClientResponseDTO> getAllClients() {
        return clientMapper.toDto(
                clientRepository.findAllByActiveAndVerified(true, true)
        );
    }

    public ClientResponseDTO updateClient(UUID id, ClientRequestDTO clientDto) throws UserNotFoundException {
        Client client = findClientOrThrow(id);
        modifyClient(client, clientDto);

        clientRepository.save(client);
        return clientMapper.toDto(client);
    }

    public Client updateClientAdmin(UUID id, ClientRequestDTO clientDto) throws UserNotFoundException {
        Client client = findClientOrThrow(id);
        modifyClient(client, clientDto);

        client.setActive(clientDto.active());
        String pass = clientDto.password();
        if (!pass.isEmpty()) {
            client.setPassword(passwordEncoder.encode(pass));
        }

        return clientRepository.save(client);
    }

    private void modifyClient(Client client, ClientRequestDTO clientDto) {
        userService.verifyNonExistenceOrThrow(
                clientDto.username(),
                clientDto.mail(),
                clientDto.cellNumber(),
                client.getUsername(),
                client.getMail(),
                client.getCellNumber()
        );

        client.setName(clientDto.name());
        client.setLastName(clientDto.lastName());
        client.setUsername(clientDto.username());
        client.setMail(clientDto.mail());
        client.setCellNumber(clientDto.cellNumber());
    }

    public Client deleteClient(UUID clientId) {
        Client client = findClientOrThrow(clientId);

        getAllReviewsByClientId(clientId).forEach(dto -> {
            Review review = reviewRepository.findByIdAndActive(dto.id(), true)
                    .orElseThrow(() -> new ReviewNotFoundException(dto.id()));

            review.setActive(false);
            reviewRepository.save(review);
        });
        reservationRepository.findByClientId(clientId).forEach(reservation -> {
            reservation.setStatus(ReservationStatus.CANCELED);
            reservationRepository.save(reservation);
        });

        client.setActive(false);
        return clientRepository.save(client);
    }

    public List<ReviewResponseDTO> getAllReviewsByClientId(UUID id) throws UserNotFoundException {
        if (clientRepository.findByIdAndActive(id, true).isEmpty()) {
            throw new UserNotFoundException(id, Role.CLIENT);
        }

        List<Review> reviews = reviewRepository.findByClientIdAndActive(id, true);
        return reviewMapper.toDto(reviews);
    }

    public ClientResponseDTO findClientById(UUID id) throws UserNotFoundException {
        return clientMapper.toDto(
                findClientOrThrow(id)
        );
    }

    public OwnerResponseDTO turnClientToOwner(UUID id) throws UserNotFoundException {
        Client client = findClientOrThrow(id);
        deleteClient(id);
        return ownerService.insertOwner(client);
    }
}
