package com.example.CanchaSystem.service;

import com.example.CanchaSystem.Mapper.EstablishmentMapper;
import com.example.CanchaSystem.Mapper.ReservationMapper;
import com.example.CanchaSystem.dto.EstablishmentRatingDTO;
import com.example.CanchaSystem.dto.request.EstablishmentRequestDTO;
import com.example.CanchaSystem.dto.response.CanchaResponseDTO;
import com.example.CanchaSystem.dto.response.EstablishmentResponseDTO;
import com.example.CanchaSystem.exception.cancha.CanchaNotFoundException;
import com.example.CanchaSystem.exception.cancha.NoCanchasException;
import com.example.CanchaSystem.exception.canchaBrand.CanchaBrandNameAlreadyExistsException;
import com.example.CanchaSystem.exception.canchaBrand.CanchaBrandNotFoundException;
import com.example.CanchaSystem.exception.misc.UnableToDropException;
import com.example.CanchaSystem.model.Brand;
import com.example.CanchaSystem.model.Cancha;
import com.example.CanchaSystem.model.CanchaType;
import com.example.CanchaSystem.model.Establishment;
import com.example.CanchaSystem.repository.CanchaBrandRepository;
import com.example.CanchaSystem.repository.CanchaRepository;
import com.example.CanchaSystem.repository.EstablishmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EstablishmentService {
    @Autowired
    private EstablishmentRepository establishmentRepository;

    @Autowired
    private CanchaRepository canchaRepository;

    @Autowired
    private CanchaService canchaService;

    @Autowired
    private ReviewService reviewService;

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

        Double avgRating = reviewService.getEstablishmentAverageRating(id);
        //return mapper.toDto(establishment);

        Map<Long, List<CanchaType>> canchaTypes = canchaService.getCanchaTypesByEstablishment();

        return new EstablishmentResponseDTO(
                id,
                establishment.getName(),
                establishment.getAddress(),
                establishment.getOpeningHour(),
                establishment.getClosingHour(),
                establishment.isCanShower(),
                establishment.getBrand().getId(),
                establishment.isActive(),
                avgRating,
                canchaTypes.getOrDefault(establishment.getId(),List.of())
        );
    }

    public EstablishmentResponseDTO insertEstablishment(EstablishmentRequestDTO establishmentDto) {
        Brand brand = brandRepository.findById(establishmentDto.brandId())
                .orElseThrow(() -> new CanchaBrandNotFoundException("Marca no encontrada"));

        if (establishmentRepository.existsByNameAndActive(establishmentDto.name(), true)) {
            throw new CanchaBrandNameAlreadyExistsException("El nombre del establecimiento ya existe");
        }

        Establishment establishment = Establishment.builder()
                .brand(brand)
                .name(establishmentDto.name())
                .address(establishmentDto.address())
                .canShower(establishmentDto.canShower())
                .openingHour(establishmentDto.openingHour())
                .closingHour(establishmentDto.closingHour())
                .active(true)
                .build();

        establishmentRepository.save(establishment);

        return mapper.toDto(establishment);
    }

    public List<EstablishmentResponseDTO> getAllActiveEstablishment() {
        List<Establishment> establishments = establishmentRepository.findByActive(true);

        if (establishments.isEmpty()) {
            throw new NoCanchasException("Todavia no hay Establecimientos registrados");
        }

        Map<Long, Double> avgRatingList = reviewService.getAllEstablishmentAverageRatings().stream()
                .collect(Collectors.toMap(EstablishmentRatingDTO::getEstablishmentId, EstablishmentRatingDTO::getAverageRating));

        Map<Long, List<CanchaType>> canchaTypes = canchaService.getCanchaTypesByEstablishment();

        return establishments.stream()
                .map(est -> new EstablishmentResponseDTO(
                        est.getId(),
                        est.getName(),
                        est.getAddress(),
                        est.getClosingHour(),
                        est.getOpeningHour(),
                        est.isCanShower(),
                        est.getBrand().getId(),
                        est.isActive(),
                        avgRatingList.getOrDefault(est.getId(),0.0),
                        canchaTypes.getOrDefault(est.getId(),List.of())
                ))
                .toList();

        //return mapper.toDto(establishments);
    }

    public void deleteEstablishment(Long establishmentId) {
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
        establishmentRepository.save(establishment);
    }

    public List<EstablishmentResponseDTO> getEstablishmentsByBrandId(Long brandId) {
        List<Establishment> establishments = establishmentRepository.findByBrandIdAndActive(brandId, true);

        if (establishments.isEmpty()) {
            throw new NoCanchasException("La marca no tiene establecimientos aun");
        }

        return mapper.toDto(establishments);
    }

    public EstablishmentResponseDTO updateEstablishment(Long id, EstablishmentRequestDTO establishmentDto) {
        Optional<Establishment> establishmentOpt = establishmentRepository.findByIdAndActive(id, true);

        if (establishmentOpt.isEmpty()) {
            throw new NoCanchasException("No se encontro un establecimiento con ese id");
        }



        Establishment establishment = establishmentOpt.get();

        if (establishmentRepository.existsByNameAndActive(establishmentDto.name(), true) && !establishment.getName().equals(establishmentDto.name())) {
            throw new CanchaBrandNameAlreadyExistsException("El nombre del establecimiento ya existe");
        }

        establishment.setAddress(establishmentDto.address());
        establishment.setName(establishmentDto.name());
        establishment.setCanShower(establishmentDto.canShower());
        establishment.setOpeningHour(establishmentDto.openingHour());
        establishment.setClosingHour(establishmentDto.closingHour());

        establishmentRepository.save(establishment);

        return mapper.toDto(establishment);
    }

    public List<EstablishmentResponseDTO> getEstablishmentsByOwnerId(UUID ownerId) {
        List<Establishment> establishments = establishmentRepository.findByBrand_Owner_IdAndActive(ownerId, true);

        if (establishments.isEmpty()) {
            throw new NoCanchasException("El dueño aun no tiene sucursales");
        }

        return mapper.toDto(establishments);
    }

    public Map<Long, Double> loadExploreRatings() {
        return reviewService.getAllEstablishmentAverageRatings().stream()
                .collect(Collectors.toMap(
                        EstablishmentRatingDTO::getEstablishmentId,
                        EstablishmentRatingDTO::getAverageRating
                ));
    }
}
