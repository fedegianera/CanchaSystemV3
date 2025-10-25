package com.example.CanchaSystem.service;

import com.example.CanchaSystem.dto.response.CanchaResponseDTO;
import com.example.CanchaSystem.dto.response.EstablishmentResponseDTO;
import com.example.CanchaSystem.exception.cancha.NoCanchasException;
import com.example.CanchaSystem.exception.canchaBrand.CanchaBrandNotFoundException;
import com.example.CanchaSystem.exception.misc.UnableToDropException;
import com.example.CanchaSystem.model.Brand;
import com.example.CanchaSystem.model.Cancha;
import com.example.CanchaSystem.model.Establishment;
import com.example.CanchaSystem.repository.CanchaRepository;
import com.example.CanchaSystem.repository.EstablishmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstablishmentService {
    @Autowired
    private EstablishmentRepository establishmentRepository;

    @Autowired
    private CanchaRepository canchaRepository;

    @Autowired
    private CanchaService canchaService;

    public List<EstablishmentResponseDTO> getAllActiveEstablishment() {
        List<EstablishmentResponseDTO> establishments = establishmentRepository.findByActive(true);

        if (establishments.isEmpty()) {
            throw new NoCanchasException("Todavia no hay Establecimientos registrados");
        }

        return establishments;
    }

    public void deleteEstablishment(Long establishmentId) {
        Establishment establishment = establishmentRepository.findById(establishmentId)
                .orElseThrow(() -> new CanchaBrandNotFoundException("Establecimiento no encontrado"));

        if (!establishment.isActive()) {
            throw new UnableToDropException("El establecimiento ya esta inactivo");
        }

        List<CanchaResponseDTO> canchas = canchaRepository.findByEstablishmentId(establishmentId);

        for (CanchaResponseDTO dto : canchas) {
            if (dto.active()) {
                canchaService.deleteCancha(dto.id());
            }
        }

        establishment.setActive(false);
        establishmentRepository.save(establishment);
    }
}
