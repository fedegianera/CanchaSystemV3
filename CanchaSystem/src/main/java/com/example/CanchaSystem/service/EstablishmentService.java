package com.example.CanchaSystem.service;

import com.example.CanchaSystem.Mapper.EstablishmentMapper;
import com.example.CanchaSystem.dto.EstablishmentNamesDTO;
import com.example.CanchaSystem.dto.EstablishmentRatingDTO;
import com.example.CanchaSystem.dto.request.EstablishmentRequestDTO;
import com.example.CanchaSystem.dto.response.EstablishmentResponseDTO;
import com.example.CanchaSystem.exception.canchaBrand.CanchaBrandNameAlreadyExistsException;
import com.example.CanchaSystem.exception.establishment.EstablishmentNotFoundException;
import com.example.CanchaSystem.model.Brand;
import com.example.CanchaSystem.model.CanchaType;
import com.example.CanchaSystem.model.Establishment;
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
    private CanchaService canchaService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private EstablishmentMapper mapper;
    @Autowired
    private CanchaBrandService canchaBrandService;

    public List<EstablishmentResponseDTO> getAllEstablishments() {
        List<Establishment> establishments = establishmentRepository.findAll();
        return mapper.toDto(establishments);
    }

    public EstablishmentResponseDTO getEstablishment(Long id){
        Establishment establishment = findEstablishmentOrThrow(id);

        Double avgRating = reviewService.getEstablishmentAverageRating(id);
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
                avgRating == null ? 0 : avgRating,
                canchaTypes.getOrDefault(establishment.getId(), List.of())
        );
    }

    public EstablishmentResponseDTO insertEstablishment(EstablishmentRequestDTO establishmentDto) {
        Brand brand = canchaBrandService.findBrandOrThrow(establishmentDto.brandId());

        verifyEstablishmentOrThrow(establishmentDto.name());

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

    public Establishment findEstablishmentOrThrow(Long id) {
        return establishmentRepository.findByIdAndActive(id, true)
                .orElseThrow(() -> new EstablishmentNotFoundException(id));
    }

    public void verifyEstablishmentOrThrow(String name, String previousName) {
        if (!name.equals(previousName) && establishmentRepository.existsByNameAndActive(name, true))
            throw new CanchaBrandNameAlreadyExistsException("El nombre del establecimiento ya existe");
    }

    public void verifyEstablishmentOrThrow(String name) {
        if (establishmentRepository.existsByNameAndActive(name, true))
            throw new CanchaBrandNameAlreadyExistsException("El nombre del establecimiento ya existe");
    }

    public List<EstablishmentResponseDTO> getAllActiveEstablishments() {
        List<Establishment> establishments = establishmentRepository.findByActive(true);
        Map<Long, Double> avgRatingList = getAverageRatings();

        Map<Long, List<CanchaType>> canchaTypes = canchaService.getCanchaTypesByEstablishment();

        return establishments.stream()
                .filter(est -> !canchaTypes.getOrDefault(est.getId(), List.of()).isEmpty())
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
                        canchaTypes.getOrDefault(est.getId(), List.of())
                ))
                .toList();
    }

    public void deleteEstablishment(Long establishmentId) {
        Establishment establishment = findEstablishmentOrThrow(establishmentId);

        canchaService.getActiveCanchasByEstablishmentId(establishmentId).forEach(cancha ->
                canchaService.deleteCancha(cancha.id()));

        establishment.setActive(false);
        establishmentRepository.save(establishment);
    }

    public List<EstablishmentResponseDTO> getEstablishmentsByBrandId(Long brandId) {
        return mapper.toDto(
                establishmentRepository.findByBrandIdAndActive(brandId, true)
        );
    }

    public EstablishmentResponseDTO updateEstablishment(Long id, EstablishmentRequestDTO establishmentDto) {
        Establishment establishment = findEstablishmentOrThrow(id);

        verifyEstablishmentOrThrow(establishmentDto.name(), establishment.getName());

        establishment.setAddress(establishmentDto.address());
        establishment.setName(establishmentDto.name());
        establishment.setCanShower(establishmentDto.canShower());
        establishment.setOpeningHour(establishmentDto.openingHour());
        establishment.setClosingHour(establishmentDto.closingHour());

        establishmentRepository.save(establishment);

        return mapper.toDto(establishment);
    }

    public List<EstablishmentResponseDTO> getEstablishmentsByOwnerId(UUID ownerId) {
        return mapper.toDto(
                establishmentRepository.findByBrand_Owner_IdAndActive(ownerId, true)
        );
    }

    public Map<Long, Double> getAverageRatings() {
        return reviewService.getAllEstablishmentAverageRatings().stream()
                .collect(Collectors.toMap(
                        EstablishmentRatingDTO::getEstablishmentId,
                        EstablishmentRatingDTO::getAverageRating
                ));
    }

    public Map<Long, String> getEstablishmentsNames(Long[] ids){
        Set<Long> idList = Set.of(ids);
        return establishmentRepository.getAllEstablishmentsNames()
                .stream()
                .filter(name -> idList.contains(name.getEstablishmentId()))
                .collect(
                        Collectors.toMap(
                                EstablishmentNamesDTO::getEstablishmentId,
                                EstablishmentNamesDTO::getName
                        )
                );
    }
}
