package com.example.CanchaSystem.service;

import com.example.CanchaSystem.dto.response.StatsResponseDTO;
import com.example.CanchaSystem.model.ReservationStatus;
import com.example.CanchaSystem.repository.ClientRepository;
import com.example.CanchaSystem.repository.OwnerRepository;
import com.example.CanchaSystem.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class StatsAdminService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private ClientRepository clientRepository;

    public StatsResponseDTO getGeneralStats() {
        long total = reservationRepository.count();
        long completed = reservationRepository.countByStatus(ReservationStatus.COMPLETED);
        long canceled = reservationRepository.countByStatus(ReservationStatus.CANCELED);
        long pending = reservationRepository.countByStatus(ReservationStatus.PENDING);

        long totalOwners = ownerRepository.countByActive(true);
        long totalClients = clientRepository.countByActive(true);

        return new StatsResponseDTO(
                total,
                completed,
                canceled,
                pending,
                totalOwners,
                totalClients
        );
    }

    public StatsResponseDTO getStatsByPeriod(LocalDateTime from, LocalDateTime until) {
        long total = reservationRepository.countByMatchDateBetween(from, until);
        long completed = reservationRepository.countByMatchDateBetweenAndStatus(from, until, ReservationStatus.COMPLETED);
        long canceled = reservationRepository.countByMatchDateBetweenAndStatus(from, until, ReservationStatus.CANCELED);
        long pending = reservationRepository.countByMatchDateBetweenAndStatus(from, until, ReservationStatus.PENDING);

        // Owners y Clients no dependen del período, se devuelven en total igual
        long totalOwners = ownerRepository.countByActive(true);
        long totalClients = clientRepository.countByActive(true);

        return new StatsResponseDTO(
                total,
                completed,
                canceled,
                pending,
                totalOwners,
                totalClients
        );
    }
}