
package com.example.CanchaSystem.service;

import com.example.CanchaSystem.interfaces.IUser;
import com.example.CanchaSystem.model.PasswordResetCode;
import com.example.CanchaSystem.repository.AdminRepository;
import com.example.CanchaSystem.repository.ClientRepository;
import com.example.CanchaSystem.repository.OwnerRepository;
import com.example.CanchaSystem.repository.PasswordResetCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class PasswordResetService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordResetCodeRepository resetCodeRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final int CODE_EXPIRATION_MINUTES = 15;

    public void requestReset(String username) {
        IUser user = findUserByUsername(username);

        String code = generateSixDigitCode();

        PasswordResetCode resetCode = new PasswordResetCode(
                username,
                code,
                LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES)
        );
        resetCodeRepository.save(resetCode);

        mailService.sendPasswordResetMail(user.getMail(), user.getUsername(), code);
    }

    public void verifyCode(String username, String code) {
        PasswordResetCode resetCode = resetCodeRepository
                .findTopByUsernameAndCodeAndUsedOrderByIdDesc(username, code, false)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código inválido"));

        if (resetCode.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El código expiró, solicitá uno nuevo");
        }

        resetCode.setVerified(true);
        resetCodeRepository.save(resetCode);
    }

    public void resetPassword(String username, String code, String newPassword) {
        PasswordResetCode resetCode = resetCodeRepository
                .findTopByUsernameAndCodeAndUsedOrderByIdDesc(username, code, false)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código inválido"));

        if (!resetCode.isVerified()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El código no fue verificado");
        }

        if (resetCode.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El código expiró, solicitá uno nuevo");
        }

        validateNewPasswordIsDifferent(username, newPassword);

        updatePassword(username, newPassword);

        resetCode.setUsed(true);
        resetCodeRepository.save(resetCode);
    }

    private void validateNewPasswordIsDifferent(String username, String newPassword) {
        IUser user = findUserByUsername(username);

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La nueva contraseña no puede ser igual a la anterior"
            );
        }
    }

    private IUser findUserByUsername(String username) {
        return clientRepository.findByUsernameAndActiveAndVerified(username, true, true)
                .map(u -> (IUser) u)
                .or(() -> ownerRepository.findByUsernameAndActive(username, true).map(u -> (IUser) u))
                .or(() -> adminRepository.findByUsername(username).map(u -> (IUser) u))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private void updatePassword(String username, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);

        var clientOpt = clientRepository.findByUsernameAndActiveAndVerified(username, true, true);
        if (clientOpt.isPresent()) {
            var client = clientOpt.get();
            client.setPassword(encodedPassword);
            clientRepository.save(client);
            return;
        }

        var ownerOpt = ownerRepository.findByUsernameAndActive(username, true);
        if (ownerOpt.isPresent()) {
            var owner = ownerOpt.get();
            owner.setPassword(encodedPassword);
            ownerRepository.save(owner);
            return;
        }

        var adminOpt = adminRepository.findByUsername(username);
        if (adminOpt.isPresent()) {
            var admin = adminOpt.get();
            admin.setPassword(encodedPassword);
            adminRepository.save(admin);
            return;
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
    }

    private String generateSixDigitCode() {
        SecureRandom random = new SecureRandom();
        int number = 100000 + random.nextInt(900000); // Entre 100000 y 999999
        return String.valueOf(number);
    }
}