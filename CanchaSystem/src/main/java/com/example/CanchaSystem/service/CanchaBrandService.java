package com.example.CanchaSystem.service;

import com.example.CanchaSystem.Mapper.BrandMapper;
import com.example.CanchaSystem.Mapper.CanchaMapper;
import com.example.CanchaSystem.dto.request.BrandRequestDTO;
import com.example.CanchaSystem.dto.response.BrandResponseDTO;
import com.example.CanchaSystem.dto.response.CanchaResponseDTO;
import com.example.CanchaSystem.dto.response.EstablishmentResponseDTO;
import com.example.CanchaSystem.exception.cancha.NoCanchasException;
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

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private CanchaMapper canchaMapper;

    public Brand insertCanchaBrand(BrandRequestDTO brandDto, String username)
            throws CanchaBrandNameAlreadyExistsException {

        if (canchaBrandRepository.existsByBrandName(brandDto.brandName())) {
            throw new CanchaBrandNameAlreadyExistsException("El nombre de la Marca ya existe");
        }

        Owner owner = ownerRepository.findByUsernameAndActive(username, true)
                .orElseThrow(() -> new OwnerNotFoundException("El dueño no existe"));

        Brand brand = brandMapper.toEntity(brandDto);
        brand.setOwner(owner);

        return canchaBrandRepository.save(brand);
    }


    public List<BrandResponseDTO> getAllCanchaBrands() throws NoCanchaBrandsException {
        List<Brand> brands = canchaBrandRepository.findAllByActive(true);

        if (brands.isEmpty())
            throw new NoCanchaBrandsException("Todavia no hay Marcas registradas");

        return brandMapper.toDto(brands);
    }

    public Brand updateCanchaBrand(Long id, BrandRequestDTO brandFromRequest) throws CanchaBrandNotFoundException {
        Brand brand = canchaBrandRepository.findById(id)
                .orElseThrow(() -> new CanchaBrandNotFoundException("Marca no encontrada"));

        brand.setBrandName(brandFromRequest.brandName());
        brand.setActive(brandFromRequest.active());

        return canchaBrandRepository.save(brand);
    }

    public void deleteCanchaBrand(Long canchaBrandId) {

        Brand brand = canchaBrandRepository.findById(canchaBrandId)
                .orElseThrow(() -> new CanchaBrandNotFoundException("Marca no encontrada"));

        if (!brand.isActive())
            throw new UnableToDropException("La marca ya esta inactiva");

        List<Establishment> establishments = establishmentRepository.findByBrandId(canchaBrandId);

        for (Establishment establishment : establishments) {
            if (establishment.isActive()) {
                establishmentService.deleteEstablishment(establishment.getId());
            }
        }

        brand.setActive(false);
        canchaBrandRepository.save(brand);
    }

    public BrandResponseDTO findCanchaBrandById(Long id) throws CanchaBrandNotFoundException {
        Optional<Brand> brandOpt = canchaBrandRepository.findByIdAndActive(id, true);

        if (brandOpt.isEmpty()) {
            throw new CanchaBrandNotFoundException("Marca no encontrada");
        }

        Brand brand = brandOpt.get();

        return brandMapper.toDto(brand);
    }

    public List<BrandResponseDTO> findCanchaBrandsByOwnerUsername(String username) throws OwnerNotFoundException {
        Optional<Owner> optOwner = ownerRepository.findByUsernameAndActive(username, true);

        if (optOwner.isEmpty())
            throw new OwnerNotFoundException("Dueño no encontrado");

        Owner owner = optOwner.get();
        List<Brand> brands = canchaBrandRepository.findByOwnerIdAndActive(owner.getId(), true);

        return brandMapper.toDto(brands);
    }


    public List<CanchaResponseDTO> getCanchasByBrandId(Long brandId) {
        List<Cancha> canchas = canchaRepository.findByEstablishmentId(brandId);

        if (canchas.isEmpty()) {
            throw new NoCanchasException("El establecimiento no tiene canchas");
        }
        return canchaMapper.toDto(canchas);
    }
}
