package com.example.CanchaSystem.service;

import com.example.CanchaSystem.dto.request.ClientRequestDTO;
import com.example.CanchaSystem.exception.client.UnactiveClientException;
import com.example.CanchaSystem.exception.misc.*;
import com.example.CanchaSystem.exception.client.ClientNotFoundException;
import com.example.CanchaSystem.exception.client.NoClientsException;
import com.example.CanchaSystem.model.Client;
import com.example.CanchaSystem.model.Reservation;
import com.example.CanchaSystem.model.Review;
import com.example.CanchaSystem.model.Role;
import com.example.CanchaSystem.repository.AdminRepository;
import com.example.CanchaSystem.repository.ClientRepository;
import com.example.CanchaSystem.repository.OwnerRepository;
import com.example.CanchaSystem.repository.RoleRepository;
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
    private AdminRepository adminRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReservationService reservationService;


    public Client insertClient(ClientRequestDTO clientDTO) {
        // TODO: admin existsByUsernameAndActive()
        if (clientRepository.existsByUsernameAndActive(clientDTO.username(), true)
                || adminRepository.existsByUsername(clientDTO.username())
                || ownerRepository.existsByUsernameAndActive(clientDTO.username(), true))
            throw new UsernameAlreadyExistsException("El nombre de usuario ya existe");
        if (clientRepository.existsByMail(clientDTO.mail()))
            throw new MailAlreadyRegisteredException("El correo ya está registrado");
        if (clientRepository.existsByCellNumber(clientDTO.cellNumber()))
            throw new CellNumberAlreadyAddedException("El número ya esta añadido");

        Role clientRole = roleRepo.findByName("CLIENT")
                .orElseGet(() -> roleRepo.save(new Role("CLIENT")));

        Client client = Client.builder()
                .name(clientDTO.name())
                .lastName(clientDTO.lastName())
                .username(clientDTO.username())
                .password(passwordEncoder.encode(clientDTO.password()))
                .mail(clientDTO.mail())
                .cellNumber(clientDTO.cellNumber())
                .active(true)
                .role(clientRole)
                .build();

        return clientRepository.save(client);
    }

    public List<Client> getAllClients() throws NoClientsException {
        return clientRepository.findAll();
    }

    public Client updateClient(Client update) throws ClientNotFoundException {
        Client client = findClientById(update.getId());

        client.setName(update.getName());
        client.setLastName(update.getLastName());
        client.setUsername(update.getUsername());
        client.setMail(update.getMail());
        client.setCellNumber(update.getCellNumber());

        return clientRepository.save(client);
    }

    public Client updateClientAdmin(Client update) throws ClientNotFoundException {
        Client client = findClientById(update.getId());

        client.setName(update.getName());
        client.setLastName(update.getLastName());
        client.setUsername(update.getUsername());
        client.setMail(update.getMail());
        client.setCellNumber(update.getCellNumber());
        client.setBankClient(update.getBankClient());
        client.setActive(update.isActive());

        String pass = update.getPassword();

        if (!pass.isEmpty()) {
            client.setPassword(passwordEncoder.encode(pass));
        }

        return clientRepository.save(client);
    }

    public Client addMoneyToClientBank(UUID clientId, double addedAmount) {
        if (addedAmount <= 0)
            throw new IllegalAmountException("Monto inválido");

        Client client = findClientById(clientId);

        if (!client.isActive())
            throw new UnactiveClientException("Cliente dado de baja");

        client.setBankClient(client.getBankClient() + addedAmount);
        return clientRepository.save(client);
    }

    public Client payFromClientBank(UUID clientId, double amountToPay) {
        if (amountToPay <= 0)
            throw new IllegalAmountException("Monto inválido");

        Client client = findClientById(clientId);

        if (!client.isActive())
            throw new UnactiveClientException("Cliente dado de baja");

        client.setBankClient(client.getBankClient()-amountToPay);
        return clientRepository.save(client);
    }

    public Client deleteClient(UUID clientId) {
        Client client = findClientById(clientId);

        if (!client.isActive())
            throw new UnableToDropException("El cliente ya está inactivo");

        List<Review> reviews = reviewService.getAllReviewsByClient(client.getUsername());
        for (Review review : reviews) {
            reviewService.deleteReview(review.getId());
        }

        List<Reservation> reservations = reservationService.findReservationsByClient(client.getUsername());
        for (Reservation reservation : reservations) {
            reservationService.cancelReservation(reservation);
        }

        client.setActive(false);
        return clientRepository.save(client);
    }

    public Client findClientById(UUID id) throws ClientNotFoundException {
        return clientRepository.findById(id)
                .orElseThrow(()-> new ClientNotFoundException("Cliente no encontrado"));
    }

    public Client findByUsernameAndActive(String username) throws  ClientNotFoundException {
        return clientRepository.findByUsernameAndActive(username, true)
                .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado"));
    }

    public boolean verifyUsername(String username) {
        return clientRepository.existsByUsernameAndActive(username, true)
                || adminRepository.existsByUsername(username)
                || ownerRepository.existsByUsernameAndActive(username, true);
    }
}
