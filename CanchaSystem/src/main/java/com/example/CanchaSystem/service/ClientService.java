package com.example.CanchaSystem.service;

import com.example.CanchaSystem.Mapper.ClientMapper;
import com.example.CanchaSystem.dto.request.ClientRequestDTO;
import com.example.CanchaSystem.dto.response.ClientResponseDTO;
import com.example.CanchaSystem.dto.response.ReviewResponseDTO;
import com.example.CanchaSystem.exception.misc.*;
import com.example.CanchaSystem.exception.client.ClientNotFoundException;
import com.example.CanchaSystem.exception.client.NoClientsException;
import com.example.CanchaSystem.model.Client;
import com.example.CanchaSystem.model.Reservation;
import com.example.CanchaSystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ClientMapper clientMapper;


    public ClientResponseDTO insertClient(ClientRequestDTO clientDTO) {
        if (clientRepository.existsByUsernameAndActive(clientDTO.username(), true) || adminRepository.existsByUsername(clientDTO.username()) || ownerRepository.existsByUsernameAndActive(clientDTO.username(), true)) {
            throw new UsernameAlreadyExistsException("El nombre de usuario ya existe");
        }

        if (clientRepository.existsByMailAndActive(clientDTO.mail(), true)) {
            throw new MailAlreadyRegisteredException("El correo ya esta registrado");
        }

        if (clientRepository.existsByCellNumberAndActive(clientDTO.cellNumber(), true)) {
            throw new CellNumberAlreadyAddedException("El numero ya esta añadido");
        }

        Client client = Client.builder()
                .name(clientDTO.name())
                .lastName(clientDTO.lastName())
                .username(clientDTO.username())
                .password(passwordEncoder.encode(clientDTO.password()))
                .mail(clientDTO.mail())
                .cellNumber(clientDTO.cellNumber())
                .active(true)
                .build();

        clientRepository.save(client);

        return clientMapper.toDto(client);
    }

    public List<ClientResponseDTO> getAllClients() throws NoClientsException {
        List<Client> clients = clientRepository.findAllByActive(true);
        return clientMapper.toDto(clients);
    }

    public ClientResponseDTO updateClient(UUID id, ClientRequestDTO clientDto) throws ClientNotFoundException {
        Client client = clientRepository.findByIdAndActive(id, true)
                .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado"));

        if ((clientRepository.existsByUsernameAndActive(clientDto.username(), true) ||
                adminRepository.existsByUsername(clientDto.username()) ||
                ownerRepository.existsByUsernameAndActive(clientDto.username(), true)) &&
                !clientDto.username().equals(client.getUsername())) {
            throw new UsernameAlreadyExistsException("El nombre de usuario ya existe");
        }

        if ((clientRepository.existsByMailAndActive(clientDto.mail(), true) ||
                ownerRepository.existsByMailAndActive(clientDto.mail(), true)) &&
                !clientDto.mail().equals(client.getMail())) {
            throw new MailAlreadyRegisteredException("El mail ya esta registrado");
        }

        if ((clientRepository.existsByCellNumberAndActive(clientDto.cellNumber(), true) ||
                ownerRepository.existsByCellNumberAndActive(clientDto.cellNumber(), true)) &&
                !clientDto.cellNumber().equals(client.getCellNumber())) {
            throw new CellNumberAlreadyAddedException("El numero de telefono ya esta registrado");
        }

        client.setName(clientDto.name());
        client.setLastName(clientDto.lastName());
        client.setUsername(clientDto.username());
        client.setMail(clientDto.mail());
        client.setCellNumber(clientDto.cellNumber());

        clientRepository.save(client);

        return clientMapper.toDto(client);
    }

    public Client updateClientAdmin(UUID id, ClientRequestDTO clientDto) throws ClientNotFoundException {
        Client client = clientRepository.findByIdAndActive(id, true)
                .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado"));

        if ((clientRepository.existsByUsernameAndActive(clientDto.username(), true) ||
                adminRepository.existsByUsername(clientDto.username()) ||
                ownerRepository.existsByUsernameAndActive(clientDto.username(), true)) &&
                !clientDto.username().equals(client.getUsername())) {
            throw new UsernameAlreadyExistsException("El nombre de usuario ya existe");
        }

        if ((clientRepository.existsByMailAndActive(clientDto.mail(), true) ||
                ownerRepository.existsByMailAndActive(clientDto.mail(), true)) &&
                !clientDto.mail().equals(client.getMail())) {
            throw new MailAlreadyRegisteredException("El mail ya esta registrado");
        }

        if ((clientRepository.existsByCellNumberAndActive(clientDto.cellNumber(), true) ||
                ownerRepository.existsByCellNumberAndActive(clientDto.cellNumber(), true)) &&
                !clientDto.cellNumber().equals(client.getCellNumber())) {
            throw new CellNumberAlreadyAddedException("El numero de telefono ya esta registrado");
        }

        client.setName(clientDto.name());
        client.setLastName(clientDto.lastName());
        client.setUsername(clientDto.username());
        client.setMail(clientDto.mail());
        client.setCellNumber(clientDto.cellNumber());
        client.setActive(clientDto.active());

        String pass = clientDto.password();

        if (!pass.isEmpty()) {
            client.setPassword(passwordEncoder.encode(pass));
        }

        return clientRepository.save(client);
    }

    public Client deleteClient(UUID clientId) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado"));

        if (!client.isActive())
            throw new UnableToDropException("El cliente ya esta inactivo");

        List<ReviewResponseDTO> reviews = reviewService.getAllReviewsByClientId(clientId);

        for (ReviewResponseDTO review : reviews) {
            reviewService.deleteReview(review.id());
        }

        List<Reservation> reservations = reservationRepository.findByClientId(clientId);

        for (Reservation reservation : reservations) {
            reservationService.cancelReservation(reservation.getId());
        }

        client.setActive(false);
        return clientRepository.save(client);
    }

    public ClientResponseDTO findClientById(UUID id) throws ClientNotFoundException {
        Optional<Client> clientOpt = clientRepository.findByIdAndActive(id, true);

        if (clientOpt.isEmpty()) {
            throw new ClientNotFoundException("Cliente no encontrado");
        }

        Client client = clientOpt.get();

        return clientMapper.toDto(client);
    }

    public boolean verifyUsername(String username) {
        return clientRepository.existsByUsernameAndActive(username, true) || adminRepository.existsByUsername(username) || ownerRepository.existsByUsernameAndActive(username, true);
    }
}
