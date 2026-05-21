package com.example.CanchaSystem.service;
import com.example.CanchaSystem.Mapper.CanchaMapper;
import com.example.CanchaSystem.dto.request.CanchaRequestDTO;
import com.example.CanchaSystem.dto.response.CanchaResponseDTO;
import com.example.CanchaSystem.exception.cancha.CanchaNameAlreadyExistsException;
import com.example.CanchaSystem.exception.cancha.CanchaNotFoundException;
import com.example.CanchaSystem.exception.cancha.IllegalCanchaAddressException;
import com.example.CanchaSystem.model.*;
import com.example.CanchaSystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CanchaService {

    @Autowired
    private CanchaRepository canchaRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private CanchaMapper mapper;

    @Autowired
    private EstablishmentService establishmentService;

    public CanchaResponseDTO insertCancha(CanchaRequestDTO canchaDTO) throws CanchaNameAlreadyExistsException, IllegalCanchaAddressException {
        Establishment establishment = establishmentService.findEstablishmentOrThrow(canchaDTO.establishmentId());

        Cancha cancha = Cancha.builder()
                .totalAmount(canchaDTO.totalAmount())
                .hasRoof(canchaDTO.hasRoof())
                .establishment(establishment)
                .canchaType(canchaDTO.canchaType())
                .working(canchaDTO.working())
                .active(true)
                .build();

        canchaRepository.save(cancha);

        return mapper.toDto(cancha);
    }

    public Cancha findCanchaOrThrow(Long id) {
        return canchaRepository.findByIdAndActive(id, true)
                .orElseThrow(() -> new CanchaNotFoundException(id));
    }

    public List<CanchaResponseDTO> getAllCanchas() {
        return mapper.toDto(
                canchaRepository.findAll()
        );
    }


    public List<CanchaResponseDTO> getCanchasByEstablishmentId(Long id) {
        return mapper.toDto(
                canchaRepository.findByEstablishmentId(id)
        );
    }

    public CanchaResponseDTO updateCancha(Long id,CanchaRequestDTO canchaDto) throws CanchaNotFoundException {
        Cancha cancha = findCanchaOrThrow(id);

        cancha.setTotalAmount(canchaDto.totalAmount());
        cancha.setActive(true);
        cancha.setHasRoof(canchaDto.hasRoof());
        cancha.setWorking(canchaDto.working());
        cancha.setCanchaType(canchaDto.canchaType());

        canchaRepository.save(cancha);

        return mapper.toDto(cancha);
    }

    public void deleteCancha(Long canchaId) {
        Cancha cancha = findCanchaOrThrow(canchaId);

        reviewRepository.findByEstablishmentIdAndActive(cancha.getEstablishment().getId(), true)
                .forEach(r -> reviewService.deleteReview(r.getId()));
        reservationRepository.findByCanchaId(canchaId)
                .forEach(r -> reservationService.cancelReservation(r.getId()));
        imageService.getEstablishmentImagesByEstablishmentId(canchaId)
                .forEach(i -> imageService.deleteImage(i.getId()));

        cancha.setActive(false);
        canchaRepository.save(cancha);
    }

    public CanchaResponseDTO findCanchaById(Long id) throws CanchaNotFoundException {
        Cancha cancha = canchaRepository.findById(id)
                .orElseThrow(() -> new CanchaNotFoundException(id));

        return mapper.toDto(cancha);
    }

    public List<CanchaResponseDTO> getAllActiveCanchas() {
        List<Cancha> canchas =  canchaRepository.findByActiveAndWorking(true, true);
        return mapper.toDto(canchas);
    }

    public List<CanchaResponseDTO> getWorkingCanchasByEstablishmentId(Long establishmentId) {
        List<Cancha> canchas = canchaRepository.findByEstablishmentIdAndActiveAndWorking(establishmentId,true, true);
        return mapper.toDto(canchas);
    }

    public List<CanchaResponseDTO> getActiveCanchasByEstablishmentId(Long establishmentId) {
        return mapper.toDto(
                canchaRepository.findByEstablishmentIdAndActive(establishmentId,true)
        );
    }

    public List<CanchaResponseDTO> getCanchasByOwnerId(UUID id) {
        List<Cancha> canchas = canchaRepository.findByEstablishment_Brand_Owner_IdAndActive(id, true);
        return mapper.toDto(canchas);
    }

    public List<CanchaType> getCanchaTypesByEstablishment(Long establishmentId) {
        return canchaRepository.findDistinctTypesByEstablishmentId(establishmentId);
    }

    public Map<Long, List<CanchaType>> getCanchaTypesByEstablishment() {
        return canchaRepository.findAllDistinctCanchaTypesGroupedByEstablishment()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                r -> (Long) r[0],
                                Collectors.mapping(
                                        r -> (CanchaType) r[1],
                                        Collectors.toList()
                                )
                        )
                );
    }

}
