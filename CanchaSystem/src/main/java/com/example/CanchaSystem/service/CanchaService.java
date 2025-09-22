package com.example.CanchaSystem.service;
import com.example.CanchaSystem.exception.cancha.CanchaNameAlreadyExistsException;
import com.example.CanchaSystem.exception.cancha.CanchaNotFoundException;
import com.example.CanchaSystem.exception.cancha.IllegalCanchaAddressException;
import com.example.CanchaSystem.exception.cancha.NoCanchasException;
import com.example.CanchaSystem.exception.misc.UnableToDropException;
import com.example.CanchaSystem.model.*;
import com.example.CanchaSystem.repository.CanchaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CanchaService {

    @Autowired
    private CanchaRepository canchaRepository;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReservationService reservationService;

    public Cancha insertCancha(Cancha cancha) throws CanchaNameAlreadyExistsException, IllegalCanchaAddressException {
        if(!canchaRepository.existsByName(cancha.getName()))
            return canchaRepository.save(cancha);
        else throw new CanchaNameAlreadyExistsException("El nombre de la cancha ya existe");
    }

    public List<Cancha> getAllCanchas() throws NoCanchasException {
        List<Cancha> canchas =  canchaRepository.findAll();
        if(canchas.isEmpty()){
            throw new NoCanchasException("Todavia no hay Canchas registradas");
        }

        return canchas;
    }

    public List<Cancha> getCanchasByOwner(String username) throws NoCanchasException {
        List<Cancha> canchas = canchaRepository.findByBrandOwnerUsername(username);
        if (canchas.isEmpty()) {
            throw new NoCanchasException("El dueño no tiene canchas registradas");
        }
        return canchas;
    }

    public List<Cancha> getCanchasByOwnerId(Long id) throws NoCanchasException {
        List<Cancha> canchas = canchaRepository.findByBrandOwnerId(id);
        if (canchas.isEmpty()) {
            throw new NoCanchasException("El dueño no tiene canchas");
        }

        return canchas;
    }

    public List<Cancha> getCanchasByBrandId(Long id) throws NoCanchasException {
        List<Cancha> canchas = canchaRepository.findByBrandId(id);
        return canchas;
    }

    public Cancha updateCancha(Long id,Cancha updated) throws CanchaNotFoundException {
        Cancha cancha = canchaRepository.findById(id)
                .orElseThrow(() -> new CanchaNotFoundException("Cancha no encontrada"));

        cancha.setName(updated.getName());
        cancha.setAddress(updated.getAddress());
        cancha.setTotalAmount(updated.getTotalAmount());
        cancha.setOpeningHour(updated.getOpeningHour());
        cancha.setClosingHour(updated.getClosingHour());
        cancha.setActive(updated.isActive());
        cancha.setHasRoof(updated.isHasRoof());
        cancha.setCanShower(updated.isCanShower());
        cancha.setWorking(updated.isWorking());
        cancha.setCanchaType(updated.getCanchaType());

        return canchaRepository.save(cancha);
    }

    public void updateOwnerCancha(Cancha updated, String username) throws CanchaNotFoundException {
        Cancha existing = canchaRepository.findByIdAndBrandOwnerUsernameAndActive(updated.getId(),username,true)
                .orElseThrow(()->new CanchaNotFoundException("No se encontro su cancha"));

        existing.setName(updated.getName());
        existing.setAddress(updated.getAddress());
        existing.setCanchaType(updated.getCanchaType());
        existing.setCanShower(updated.isCanShower());
        existing.setBrand(updated.getBrand());
        existing.setOpeningHour(updated.getOpeningHour());
        existing.setClosingHour(updated.getClosingHour());
        existing.setTotalAmount(updated.getTotalAmount());
        existing.setWorking(updated.isWorking());

        canchaRepository.save(existing);
    }

    public void deleteCancha(Long canchaId) {

        Cancha cancha = canchaRepository.findById(canchaId)
                .orElseThrow(() -> new CanchaNotFoundException("Cancha no encontrada"));

        if (!cancha.isActive())
            throw new UnableToDropException("La cancha ya esta inactivo");

        List<Review> reviews = reviewService.getAllReviewsByCanchaId(cancha.getId());

        for (Review review : reviews) {
            reviewService.deleteReview(review.getId());
        }

        List<Reservation> reservations = reservationService.findReservationsByCanchaId(cancha.getId());

        for (Reservation reservation : reservations) {
            reservationService.cancelReservation(reservation);
        }

        cancha.setActive(false);
        cancha.setWorking(false);
        canchaRepository.save(cancha);
    }

    public void deleteOwnerCancha(Long canchaId, String username) {
        if (canchaRepository.existsByIdAndBrandOwnerUsername(canchaId, username)) {
            Optional<Cancha> canchaOpt = canchaRepository.findById(canchaId);

            deleteCancha(canchaOpt.get().getId());
        } else throw new CanchaNotFoundException("Cancha de dueño no encontrada");
    }

    public Cancha findCanchaById(Long id) throws CanchaNotFoundException {
        return canchaRepository.findById(id).orElseThrow(()-> new CanchaNotFoundException("Cancha no encontrada"));
    }

    public List<Cancha> getAllActiveCanchas() throws NoCanchasException {
        List<Cancha> canchas =  canchaRepository.findByActiveAndWorking(true, true);
        if(canchas.isEmpty()){
            throw new NoCanchasException("Todavia no hay Canchas activas");
        }

        return canchas;
    }

    public List<Cancha> getActiveCanchasByOwnerId(Long ownerId) throws NoCanchasException {
        List<Cancha> canchas = canchaRepository.findByBrandOwnerIdAndActive(ownerId,true);
        if (canchas.isEmpty()) {
            throw new NoCanchasException("El dueño no tiene canchas activas");
        }

        return canchas;
    }

    public List<Cancha> getActiveCanchasByBrandId(Long brandId) throws NoCanchasException {
        List<Cancha> canchas = canchaRepository.findByBrandIdAndActiveAndWorking(brandId,true, true);
        if (canchas.isEmpty()) {
            throw new NoCanchasException("La marca no tiene canchas activas");
        }

        return canchas;
    }

}
