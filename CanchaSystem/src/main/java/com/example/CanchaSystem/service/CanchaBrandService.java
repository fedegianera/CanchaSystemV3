package com.example.CanchaSystem.service;

import com.example.CanchaSystem.exception.canchaBrand.CanchaBrandNameAlreadyExistsException;
import com.example.CanchaSystem.exception.canchaBrand.CanchaBrandNotFoundException;
import com.example.CanchaSystem.exception.canchaBrand.NoCanchaBrandsException;
import com.example.CanchaSystem.exception.misc.UnableToDropException;
import com.example.CanchaSystem.exception.owner.OwnerNotFoundException;
import com.example.CanchaSystem.model.Cancha;
import com.example.CanchaSystem.model.Brand;
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

    public Brand insertCanchaBrand(Brand brand) throws CanchaBrandNameAlreadyExistsException {
        if (!canchaBrandRepository.existsByBrandName(brand.getBrandName())) {
            return canchaBrandRepository.save(brand);
        } else throw new CanchaBrandNameAlreadyExistsException("El nombre de la Marca ya existe");
    }

    public List<Brand> getAllCanchaBrands() throws NoCanchaBrandsException {
        List<Brand> brands = canchaBrandRepository.findAll();
        if (brands.isEmpty())
            throw new NoCanchaBrandsException("Todavia no hay Marcas registradas");
        return brands;
    }

    public Brand updateCanchaBrand(Brand brandFromRequest) throws CanchaBrandNotFoundException {
        Brand brand = canchaBrandRepository.findById(brandFromRequest.getId())
                .orElseThrow(() -> new CanchaBrandNotFoundException("Marca no encontrada"));

        brand.setBrandName(brandFromRequest.getBrandName());
        brand.setActive(brandFromRequest.isActive());

        return canchaBrandRepository.save(brand);
    }

    public void deleteCanchaBrand(Long canchaBrandId) {

        Brand brand = canchaBrandRepository.findById(canchaBrandId)
                .orElseThrow(() -> new CanchaBrandNotFoundException("Marca no encontrada"));

        if (!brand.isActive())
            throw new UnableToDropException("La marca ya esta inactiva");

        List<Cancha> canchas = canchaRepository.findByBrandId(canchaBrandId);

        for (Cancha cancha : canchas) {
            if (cancha.isActive()) {
                canchaService.deleteCancha(cancha.getId());
            }
        }

        brand.setActive(false);
        canchaBrandRepository.save(brand);
    }

    public Brand findCanchaBrandById(Long id) throws CanchaBrandNotFoundException {
        return canchaBrandRepository.findById(id).orElseThrow(()-> new CanchaBrandNotFoundException("Marca no encontrada"));
    }

    public List<Brand> findCanchaBrandsByOwnerUsername(String username) throws OwnerNotFoundException {
        Optional<Owner> optOwner = ownerRepository.findByUsernameAndActive(username, true);

        if (optOwner.isEmpty())
            throw new OwnerNotFoundException("Dueño no encontrado");

        Owner owner = optOwner.get();
        List<Brand> brands = canchaBrandRepository.findByOwnerIdAndActive(owner.getId(), true);

        return brands;
    }


    public List<Cancha> getCanchasByBrandId(Long brandId) {
        return canchaRepository.findByBrandId(brandId);
    }
}
