package com.example.CanchaSystem.service;

import com.example.CanchaSystem.exception.UserVerificationNotFoundException;
import com.example.CanchaSystem.model.Client;
import com.example.CanchaSystem.model.UserVerification;
import com.example.CanchaSystem.repository.ClientRepository;
import com.example.CanchaSystem.repository.UserVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserVerificationService {
    @Autowired
    private UserVerificationRepository userVerificationRepository;
    @Autowired
    private ClientRepository clientRepository;

    public UserVerification findUserVerificationOrThrow(UUID userVerificationId) {
        return userVerificationRepository.findById(userVerificationId)
                .orElseThrow(() -> new UserVerificationNotFoundException(userVerificationId));
    }

    public void verifyClient(String userVerificationId) throws UserVerificationNotFoundException {
        verifyClient(UUID.fromString(userVerificationId));
    }

    public void verifyClient(UUID userVerificationId) throws UserVerificationNotFoundException {
        UserVerification verification = findUserVerificationOrThrow(userVerificationId);

        if (isExpired(verification)) {
            cascadeDeleteUserVerification(verification);
        } else {
            Client client = verification.getClient();
            client.setVerified(true);
            deleteUserVerification(verification);
            clientRepository.save(client);
        }
    }

    public UserVerification createUserVerification(Client client) {
        return userVerificationRepository.save(new UserVerification(
                null,
                client,
                System.currentTimeMillis() + TimeUnit.HOURS.toMillis(2)
        ));
    }

    public void cascadeDeleteUserVerification(UserVerification verification) {
        userVerificationRepository.delete(verification);
        clientRepository.delete(verification.getClient());
    }

    public void deleteUserVerification(UserVerification verification) {
        userVerificationRepository.delete(verification);
    }

    public boolean isExpired(UserVerification verification) {
        return verification.getExpiryTime() < System.currentTimeMillis();
    }

    @Scheduled(timeUnit = TimeUnit.HOURS, fixedRate = 1)
    private void deleteExpiredUserVerifications() {
        userVerificationRepository
                .findExpired(System.currentTimeMillis())
                .forEach(this::cascadeDeleteUserVerification);
    }
}
