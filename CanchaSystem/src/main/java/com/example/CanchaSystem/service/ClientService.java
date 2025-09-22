package com.example.CanchaSystem.service;

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


    public Client insertClient(Client client) {
        if (clientRepository.existsByUsernameAndActive(client.getUsername(), true) || adminRepository.existsByUsername(client.getUsername()) || ownerRepository.existsByUsernameAndActive(client.getUsername(), true)) {
            throw new UsernameAlreadyExistsException("El nombre de usuario ya existe");
        }

        if (clientRepository.existsByMail(client.getMail())) {
            throw new MailAlreadyRegisteredException("El correo ya esta registrado");
        }

        if (clientRepository.existsByCellNumber(client.getCellNumber())) {
            throw new CellNumberAlreadyAddedException("El numero ya esta añadido");
        }

        Role clientRole = roleRepo.findByName("CLIENT")
                .orElseGet(() -> roleRepo.save(new Role("CLIENT")));
        client.setRole(clientRole);

        client.setPassword(passwordEncoder.encode(client.getPassword()));

        return clientRepository.save(client);
    }

    public List<Client> getAllClients() throws NoClientsException {
        List<Client> clients = clientRepository.findAll();
        if(clients.isEmpty())
            throw new NoClientsException("Todavia no hay clientes registrados");
        return clients;

    }

    public Client updateClient(Client clientFromRequest) throws ClientNotFoundException {
        Client client = clientRepository.findById(clientFromRequest.getId())
                .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado"));

        client.setName(clientFromRequest.getName());
        client.setLastName(clientFromRequest.getLastName());
        client.setUsername(clientFromRequest.getUsername());
        client.setMail(clientFromRequest.getMail());
        client.setCellNumber(clientFromRequest.getCellNumber());

        return clientRepository.save(client);
    }

    public Client updateClientAdmin(Client clientFromRequest) throws ClientNotFoundException {
        Client client = clientRepository.findById(clientFromRequest.getId())
                .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado"));

        client.setName(clientFromRequest.getName());
        client.setLastName(clientFromRequest.getLastName());
        client.setUsername(clientFromRequest.getUsername());
        client.setMail(clientFromRequest.getMail());
        client.setCellNumber(clientFromRequest.getCellNumber());
        client.setBankClient(clientFromRequest.getBankClient());
        client.setActive(clientFromRequest.isActive());

        String pass = clientFromRequest.getPassword();

        if (!pass.isEmpty()) {
            client.setPassword(passwordEncoder.encode(pass));
        }

        return clientRepository.save(client);
    }

    public Client addMoneyToClientBank(Long clientId,double addedAmount){

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado"));

        if (!client.isActive())
            throw new UnactiveClientException("Cliente dado de baja");

        if (addedAmount <= 0)
            throw new IllegalAmountException("Monto invalido");

        client.setBankClient(client.getBankClient()+addedAmount);
        return clientRepository.save(client);

    }

    public Client payFromClientBank(Long clientId, double amountToPay){

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado"));

        if (!client.isActive())
            throw new UnactiveClientException("Cliente dado de baja");

        if (amountToPay <= 0)
            throw new IllegalAmountException("Monto invalido");

        client.setBankClient(client.getBankClient()-amountToPay);
        return clientRepository.save(client);

    }

    public Client deleteClient(Long clientId) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado"));

        if (!client.isActive())
            throw new UnableToDropException("El cliente ya esta inactivo");

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

    public Client findClientById(Long id) throws ClientNotFoundException {
        return clientRepository.findById(id).orElseThrow(()-> new ClientNotFoundException("Cliente no encontrado"));
    }

    public boolean verifyUsername(String username) {
        return clientRepository.existsByUsernameAndActive(username, true) || adminRepository.existsByUsername(username) || ownerRepository.existsByUsernameAndActive(username, true);
    }
}
