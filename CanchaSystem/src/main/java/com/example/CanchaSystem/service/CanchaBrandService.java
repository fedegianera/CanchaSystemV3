package com.example.CanchaSystem.service;

import com.example.CanchaSystem.exception.canchaBrand.CanchaBrandNameAlreadyExistsException;
import com.example.CanchaSystem.exception.canchaBrand.CanchaBrandNotFoundException;
import com.example.CanchaSystem.exception.canchaBrand.NoCanchaBrandsException;
import com.example.CanchaSystem.exception.misc.UnableToDropException;
import com.example.CanchaSystem.exception.owner.OwnerNotFoundException;
import com.example.CanchaSystem.model.Cancha;
import com.example.CanchaSystem.model.CanchaBrand;
import com.example.CanchaSystem.model.Owner;
import com.example.CanchaSystem.repository.CanchaBrandRepository;
import com.example.CanchaSystem.repository.CanchaRepository;
import com.example.CanchaSystem.repository.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CanchaBrandService {

    @Autowired
    private CanchaBrandRepository canchaBrandRepository;

    @Autowired
    private CanchaRepository canchaRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private CanchaService canchaService;

    public CanchaBrand insertCanchaBrand(CanchaBrand canchaBrand) throws CanchaBrandNameAlreadyExistsException {
        if (!canchaBrandRepository.existsByBrandName(canchaBrand.getBrandName())) {
            return canchaBrandRepository.save(canchaBrand);
        } else throw new CanchaBrandNameAlreadyExistsException("El nombre de la Marca ya existe");
    }

    public List<CanchaBrand> getAllCanchaBrands() throws NoCanchaBrandsException {
        List<CanchaBrand> brands = canchaBrandRepository.findAll();
        if (brands.isEmpty())
            throw new NoCanchaBrandsException("Todavia no hay Marcas registradas");
        return brands;
    }

    public CanchaBrand updateCanchaBrand(CanchaBrand canchaBrandFromRequest) throws CanchaBrandNotFoundException {
        CanchaBrand canchaBrand = canchaBrandRepository.findById(canchaBrandFromRequest.getId())
                .orElseThrow(() -> new CanchaBrandNotFoundException("Marca no encontrada"));

        canchaBrand.setBrandName(canchaBrandFromRequest.getBrandName());
        canchaBrand.setActive(canchaBrandFromRequest.isActive());

        return canchaBrandRepository.save(canchaBrand);
    }

    public void deleteCanchaBrand(Long canchaBrandId) {

        CanchaBrand canchaBrand = canchaBrandRepository.findById(canchaBrandId)
                .orElseThrow(() -> new CanchaBrandNotFoundException("Marca no encontrada"));

        if (!canchaBrand.isActive())
            throw new UnableToDropException("La marca ya esta inactiva");

        List<Cancha> canchas = canchaRepository.findByBrandId(canchaBrandId);

        for (Cancha cancha : canchas) {
            if (cancha.isActive()) {
                canchaService.deleteCancha(cancha.getId());
            }
        }

        canchaBrand.setActive(false);
        canchaBrandRepository.save(canchaBrand);
    }

    public CanchaBrand findCanchaBrandById(Long id) throws CanchaBrandNotFoundException {
        return canchaBrandRepository.findById(id).orElseThrow(()-> new CanchaBrandNotFoundException("Marca no encontrada"));
    }

    public List<CanchaBrand> findCanchaBrandsByOwnerUsername(String username) throws OwnerNotFoundException {
        Optional<Owner> optOwner = ownerRepository.findByUsernameAndActive(username, true);

        if (optOwner.isEmpty())
            throw new OwnerNotFoundException("Dueño no encontrado");

        Owner owner = optOwner.get();
        List<CanchaBrand> canchaBrands = canchaBrandRepository.findByOwnerIdAndActive(owner.getId(), true);

        return canchaBrands;
    }


    public List<Cancha> getCanchasByBrandId(Long brandId) {
        return canchaRepository.findByBrandId(brandId);
    }
}
