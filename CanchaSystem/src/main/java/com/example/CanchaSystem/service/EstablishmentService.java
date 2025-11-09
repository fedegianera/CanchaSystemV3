package com.example.CanchaSystem.service;

import com.example.CanchaSystem.Mapper.EstablishmentMapper;
import com.example.CanchaSystem.Mapper.ReservationMapper;
import com.example.CanchaSystem.dto.request.EstablishmentRequestDTO;
import com.example.CanchaSystem.dto.response.CanchaResponseDTO;
import com.example.CanchaSystem.dto.response.EstablishmentResponseDTO;
import com.example.CanchaSystem.exception.cancha.CanchaNotFoundException;
import com.example.CanchaSystem.exception.cancha.NoCanchasException;
import com.example.CanchaSystem.exception.canchaBrand.CanchaBrandNotFoundException;
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
import java.util.Optional;

@Service
public class EstablishmentService {
    @Autowired
    private EstablishmentRepository establishmentRepository;

    @Autowired
    private CanchaRepository canchaRepository;

    @Autowired
    private CanchaService canchaService;

    @Autowired
    private CanchaBrandRepository brandRepository;

    @Autowired
    private EstablishmentMapper mapper;

    public List<EstablishmentResponseDTO> getAllEstablishments() {
        List<Establishment> establishments = establishmentRepository.findAll();

        if (establishments.isEmpty()) {
            throw new NoCanchasException("Todavia no hay establecimientos registrados");
        }

        return mapper.toDto(establishments);
    }

    public EstablishmentResponseDTO getEstablishment(Long id){
        Establishment establishment = establishmentRepository.findByIdAndActive(id, true).orElseThrow(
                () -> new CanchaNotFoundException("Hubo problemas al buscar el establecimiento"));

        return mapper.toDto(establishment);
    }

    public Establishment insertEstablishment(EstablishmentRequestDTO establishmentDto) {
        Brand brand = brandRepository.findById(establishmentDto.brandId())
                .orElseThrow(() -> new CanchaBrandNotFoundException("Marca no encontrada"));

        Establishment establishment = Establishment.builder()
                .brand(brand)
                .name(establishmentDto.name())
                .address(establishmentDto.address())
                .canShower(establishmentDto.canShower())
                .openingHour(establishmentDto.openingHour())
                .closingHour(establishmentDto.closingHour())
                .active(true)
                .build();

        return establishmentRepository.save(establishment);
    }

    public List<EstablishmentResponseDTO> getAllActiveEstablishment() {
        List<Establishment> establishments = establishmentRepository.findByActive(true);

        if (establishments.isEmpty()) {
            throw new NoCanchasException("Todavia no hay Establecimientos registrados");
        }

        return mapper.toDto(establishments);
    }

    public Establishment deleteEstablishment(Long establishmentId) {
        Establishment establishment = establishmentRepository.findById(establishmentId)
                .orElseThrow(() -> new CanchaBrandNotFoundException("Establecimiento no encontrado"));

        if (!establishment.isActive()) {
            throw new UnableToDropException("El establecimiento ya esta inactivo");
        }

        List<Cancha> canchas = canchaRepository.findByEstablishmentId(establishmentId);

        for (Cancha cancha : canchas) {
            if (cancha.isActive()) {
                canchaService.deleteCancha(cancha.getId());
            }
        }

        establishment.setActive(false);
        return establishmentRepository.save(establishment);
    }

    public List<EstablishmentResponseDTO> getEstablishmentsByBrandId(Long brandId) {
        List<Establishment> establishments = establishmentRepository.findByBrandIdAndActive(brandId, true);

        if (establishments.isEmpty()) {
            throw new NoCanchasException("La marca no tiene establecimientos aun");
        }

        return mapper.toDto(establishments);
    }

    public Establishment updateEstablishment(Long id, EstablishmentRequestDTO establishmentDto) {
        Optional<Establishment> establishmentOpt = establishmentRepository.findByIdAndActive(id, true);

        if (establishmentOpt.isEmpty()) {
            throw new NoCanchasException("No se encontro un establecimiento con ese id");
        }

        Establishment establishment = establishmentOpt.get();

        establishment.setAddress(establishmentDto.address());
        establishment.setName(establishmentDto.name());
        establishment.setCanShower(establishmentDto.canShower());
        establishment.setOpeningHour(establishmentDto.openingHour());
        establishment.setClosingHour(establishmentDto.closingHour());

        return establishmentRepository.save(establishment);
    }
}
