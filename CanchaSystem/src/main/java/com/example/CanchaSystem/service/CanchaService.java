package com.example.CanchaSystem.service;
import com.example.CanchaSystem.Mapper.CanchaMapper;
import com.example.CanchaSystem.dto.request.CanchaRequestDTO;
import com.example.CanchaSystem.dto.response.CanchaResponseDTO;
import com.example.CanchaSystem.dto.response.EstablishmentResponseDTO;
import com.example.CanchaSystem.exception.cancha.CanchaNameAlreadyExistsException;
import com.example.CanchaSystem.exception.cancha.CanchaNotFoundException;
import com.example.CanchaSystem.exception.cancha.IllegalCanchaAddressException;
import com.example.CanchaSystem.exception.cancha.NoCanchasException;
import com.example.CanchaSystem.exception.misc.UnableToDropException;
import com.example.CanchaSystem.model.*;
import com.example.CanchaSystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CanchaService {

    @Autowired
    private CanchaRepository canchaRepository;

    @Autowired
    private CanchaBrandRepository brandRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private EstablishmentRepository establishmentRepository;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private CanchaMapper mapper;

    public CanchaResponseDTO insertCancha(CanchaRequestDTO canchaDTO) throws CanchaNameAlreadyExistsException, IllegalCanchaAddressException {
        Establishment establishment = establishmentRepository.findById(canchaDTO.establishmentId())
                .orElseThrow(() -> new CanchaNotFoundException("Marca no encontrada"));

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

    public List<CanchaResponseDTO> getAllCanchas() throws NoCanchasException {
        List<Cancha> canchas =  canchaRepository.findAll();
        if(canchas.isEmpty()){
            throw new NoCanchasException("Todavia no hay Canchas registradas");
        }

        return mapper.toDto(canchas);
    }


    public List<CanchaResponseDTO> getCanchasByEstablishmentId(Long id) throws NoCanchasException {
        List<Cancha> canchas = canchaRepository.findByEstablishmentId(id);

        if (canchas.isEmpty()) {
            throw new NoCanchasException("El establecimiento no tiene canchas");
        }
        return mapper.toDto(canchas);
    }

    public CanchaResponseDTO updateCancha(Long id,CanchaRequestDTO canchaDto) throws CanchaNotFoundException {
        Cancha cancha = canchaRepository.findById(id)
                .orElseThrow(() -> new CanchaNotFoundException("Cancha no encontrada"));

        cancha.setTotalAmount(canchaDto.totalAmount());
        cancha.setActive(true);
        cancha.setHasRoof(canchaDto.hasRoof());
        cancha.setWorking(canchaDto.working());
        cancha.setCanchaType(canchaDto.canchaType());

        canchaRepository.save(cancha);

        return mapper.toDto(cancha);
    }

    public void deleteCancha(Long canchaId) {

        Cancha cancha = canchaRepository.findById(canchaId)
                .orElseThrow(() -> new CanchaNotFoundException("Cancha no encontrada"));

        if (!cancha.isActive())
            throw new UnableToDropException("La cancha ya esta inactivo");

        List<Review> reviews = reviewRepository.findByEstablishmentIdAndActive(cancha.getEstablishment().getId(), true);

        for (Review review : reviews) {
            reviewService.deleteReview(review.getId());
        }

        List<Reservation> reservations = reservationRepository.findByCanchaId(canchaId);

        for (Reservation reservation : reservations) {
            reservationService.cancelReservation(reservation.getId());
        }

        cancha.setActive(false);
        cancha.setWorking(false);
        canchaRepository.save(cancha);
    }

    public CanchaResponseDTO findCanchaById(Long id) throws CanchaNotFoundException {
        Optional<Cancha> canchaOpt = canchaRepository.findById(id);

        if (canchaOpt.isEmpty()) {
            throw new CanchaNotFoundException("Cancha no encontrada");
        }

        Cancha cancha = canchaOpt.get();


        return mapper.toDto(cancha);
    }

    public List<CanchaResponseDTO> getAllActiveCanchas() throws NoCanchasException {
        List<Cancha> canchas =  canchaRepository.findByActiveAndWorking(true, true);

        if(canchas.isEmpty()){
            throw new NoCanchasException("Todavia no hay Canchas activas");
        }

        return mapper.toDto(canchas);
    }

    public List<CanchaResponseDTO> getActiveCanchasByEstablishmentId(Long establishmentId) throws NoCanchasException {
        List<Cancha> canchas = canchaRepository.findByEstablishmentIdAndActiveAndWorking(establishmentId,true, true);

        if (canchas.isEmpty()) {
            throw new NoCanchasException("La marca no tiene canchas activas");
        }

        return mapper.toDto(canchas);
    }

    public List<CanchaResponseDTO> getCanchasByOwnerId(UUID id) {
        List<Cancha> canchas = canchaRepository.findByEstablishment_Brand_Owner_IdAndActive(id, true);

        if (canchas.isEmpty()) {
            throw new NoCanchasException("El dueño aun no tiene canchas");
        }

        return mapper.toDto(canchas);
    }

    public List<CanchaType> getCanchaTypesByEstablishment(Long establishmentId) {
        return canchaRepository.findDistinctTypesByEstablishmentId(establishmentId);
    }

    public Map<Long, List<CanchaType>> getCanchaTypesByEstablishment() {

        List<Object[]> rows = canchaRepository.findAllEstablishmentCanchaTypes();

        Map<Long, List<CanchaType>> result = new HashMap<>();

        for (Object[] row : rows) {
            Long estId = (Long) row[0];
            CanchaType type = (CanchaType) row[1];

            result.computeIfAbsent(estId, k -> new ArrayList<>()).add(type);
        }

        return result;
    }

}
