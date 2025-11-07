package com.example.CanchaSystem.service;

import com.example.CanchaSystem.dto.request.EstablishmentRequestDTO;
import com.example.CanchaSystem.dto.response.CanchaResponseDTO;
import com.example.CanchaSystem.dto.response.EstablishmentResponseDTO;
import com.example.CanchaSystem.exception.cancha.CanchaNotFoundException;
import com.example.CanchaSystem.exception.cancha.NoCanchasException;
import com.example.CanchaSystem.exception.canchaBrand.CanchaBrandNameAlreadyExistsException;
import com.example.CanchaSystem.exception.canchaBrand.CanchaBrandNotFoundException;
import com.example.CanchaSystem.exception.canchaBrand.NoCanchaBrandsException;
import com.example.CanchaSystem.exception.establishment.EstablishmentNameAlreadyExistsException;
import com.example.CanchaSystem.exception.misc.UnableToDropException;
import com.example.CanchaSystem.model.Brand;
import com.example.CanchaSystem.model.Cancha;
import com.example.CanchaSystem.model.Establishment;
import com.example.CanchaSystem.repository.CanchaBrandRepository;
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
    private CanchaBrandRepository canchaBrandRepository;

    @Autowired
    private CanchaService canchaService;


    public List<EstablishmentResponseDTO> getAllActiveEstablishment() {
        List<EstablishmentResponseDTO> establishments = establishmentRepository.findByActive(true);

        if (establishments.isEmpty()) {
            throw new NoCanchasException("Todavia no hay Establecimientos registrados");
        }

        return establishments;
    }


    public Establishment insertEstablishment(EstablishmentRequestDTO establishment) throws EstablishmentNameAlreadyExistsException {
        if (!establishmentRepository.existsByName(establishment.name())) {
            Brand brand = canchaBrandRepository.findById(establishment.brandId())
                    .orElseThrow(() -> new CanchaBrandNotFoundException("Marca no encontrada"));

            Establishment establishment1 = Establishment.builder()
                    .name(establishment.name())
                    .address(establishment.address())
                    .brand(brand)
                    .canShower(establishment.canShower())
                    .openingHour(establishment.openingHour())
                    .closingHour(establishment.closingHour())
                    .build();



            return establishmentRepository.save(establishment1);
        } else throw new EstablishmentNameAlreadyExistsException("El nombre del establecimiento ya existe");
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
