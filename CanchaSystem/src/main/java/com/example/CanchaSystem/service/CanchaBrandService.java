package com.example.CanchaSystem.service;

import com.example.CanchaSystem.dto.response.CanchaResponseDTO;
import com.example.CanchaSystem.dto.response.EstablishmentResponseDTO;
import com.example.CanchaSystem.exception.canchaBrand.CanchaBrandNameAlreadyExistsException;
import com.example.CanchaSystem.exception.canchaBrand.CanchaBrandNotFoundException;
import com.example.CanchaSystem.exception.canchaBrand.NoCanchaBrandsException;
import com.example.CanchaSystem.exception.misc.UnableToDropException;
import com.example.CanchaSystem.exception.owner.OwnerNotFoundException;
import com.example.CanchaSystem.model.Cancha;
import com.example.CanchaSystem.model.Brand;
import com.example.CanchaSystem.model.Establishment;
import com.example.CanchaSystem.model.Owner;
import com.example.CanchaSystem.repository.CanchaBrandRepository;
import com.example.CanchaSystem.repository.CanchaRepository;
import com.example.CanchaSystem.repository.EstablishmentRepository;
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
    private EstablishmentRepository establishmentRepository;

    @Autowired
    private CanchaRepository canchaRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private CanchaService canchaService;

    @Autowired
    private EstablishmentService establishmentService;

    public Brand insertCanchaBrand(Brand brand) throws CanchaBrandNameAlreadyExistsException {
        if (canchaBrandRepository.existsByBrandName(brand.getBrandName()))
            throw new CanchaBrandNameAlreadyExistsException("El nombre de la Marca ya existe");

        return canchaBrandRepository.save(brand);
    }

    public List<Brand> getAllCanchaBrands() throws NoCanchaBrandsException {
        return canchaBrandRepository.findAll();
    }

    public Brand updateCanchaBrand(Brand updated) throws CanchaBrandNotFoundException {
        Brand brand = findCanchaBrandById(updated.getId());

        brand.setBrandName(updated.getBrandName());
        brand.setActive(updated.isActive());

        return canchaBrandRepository.save(brand);
    }

    public void deleteCanchaBrand(Long canchaBrandId) {
        Brand brand = findCanchaBrandById(canchaBrandId);

        if (!brand.isActive())
            throw new UnableToDropException("La marca ya está inactiva");

        List<EstablishmentResponseDTO> establishments = establishmentRepository.findByBrandId(canchaBrandId);
        for (EstablishmentResponseDTO dto : establishments) {
            if (dto.active()) {
                establishmentService.deleteEstablishment(dto.id());
            }
        }

        brand.setActive(false);
        canchaBrandRepository.save(brand);
    }

    public Brand findCanchaBrandById(Long id) throws CanchaBrandNotFoundException {
        return canchaBrandRepository.findById(id)
                .orElseThrow(()-> new CanchaBrandNotFoundException("Marca no encontrada"));
    }

    public List<Brand> findCanchaBrandsByOwnerUsername(String username) throws OwnerNotFoundException {
        Optional<Owner> optOwner = ownerRepository.findByUsernameAndActive(username, true);

        if (optOwner.isEmpty())
            throw new OwnerNotFoundException("Dueño no encontrado");

        Owner owner = optOwner.get();

        return canchaBrandRepository.findByOwnerIdAndActive(owner.getId(), true);
    }


    public List<CanchaResponseDTO> getCanchasByBrandId(Long brandId) {
        return canchaRepository.findByEstablishmentId(brandId);
    }
}
