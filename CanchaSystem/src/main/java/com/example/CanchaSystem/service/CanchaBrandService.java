package com.example.CanchaSystem.service;

import com.example.CanchaSystem.Mapper.BrandMapper;
import com.example.CanchaSystem.Mapper.CanchaMapper;
import com.example.CanchaSystem.dto.request.BrandRequestDTO;
import com.example.CanchaSystem.dto.response.BrandResponseDTO;
import com.example.CanchaSystem.dto.response.CanchaResponseDTO;
import com.example.CanchaSystem.exception.canchaBrand.CanchaBrandNameAlreadyExistsException;
import com.example.CanchaSystem.exception.canchaBrand.BrandNotFoundException;
import com.example.CanchaSystem.exception.user.UserNotFoundException;
import com.example.CanchaSystem.model.*;
import com.example.CanchaSystem.repository.CanchaBrandRepository;
import com.example.CanchaSystem.repository.CanchaRepository;
import com.example.CanchaSystem.repository.EstablishmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CanchaBrandService {

    @Autowired
    private CanchaBrandRepository canchaBrandRepository;

    @Autowired
    private EstablishmentRepository establishmentRepository;

    @Autowired
    private CanchaRepository canchaRepository;

    @Autowired
    private EstablishmentService establishmentService;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private CanchaMapper canchaMapper;

    @Autowired
    private OwnerService ownerService;

    public BrandResponseDTO insertCanchaBrand(BrandRequestDTO brandDto, String username)
            throws UserNotFoundException, CanchaBrandNameAlreadyExistsException {
        verifyNonExistenceOrThrow(brandDto.brandName());

        Owner owner = ownerService.findOwnerOrThrow(username);
        Brand brand = brandMapper.toEntity(brandDto);
        brand.setOwner(owner);
        brand.setActive(true);

        canchaBrandRepository.save(brand);
        return brandMapper.toDto(brand);
    }


    public List<BrandResponseDTO> getAllCanchaBrands() {
        List<Brand> brands = canchaBrandRepository.findAllByActive(true);

        System.out.println("-----------------------------------------------------------------");
        System.out.println(brands);

        return brandMapper.toDto(brands);
    }

    public BrandResponseDTO updateCanchaBrand(Long id, BrandRequestDTO brandFromRequest) throws BrandNotFoundException {
        Brand brand = findBrandOrThrow(id);
        verifyNonExistenceOrThrow(brandFromRequest.brandName());

        brand.setBrandName(brandFromRequest.brandName());
        brand.setActive(true);

        canchaBrandRepository.save(brand);
        return brandMapper.toDto(brand);
    }

    public void deleteCanchaBrand(Long canchaBrandId) {
        Brand brand = findBrandOrThrow(canchaBrandId);

        establishmentRepository.findByBrandIdAndActive(canchaBrandId, true).forEach(establishment ->
                establishmentService.deleteEstablishment(establishment.getId()));

        brand.setActive(false);
        canchaBrandRepository.save(brand);
    }

    public Brand findBrandOrThrow(Long id) throws BrandNotFoundException {
        return canchaBrandRepository.findByIdAndActive(id, true)
                .orElseThrow(() -> new BrandNotFoundException(id));
    }

    public void verifyNonExistenceOrThrow(String brandName) {
        if (canchaBrandRepository.existsByBrandNameAndActive(brandName, true))
            throw new CanchaBrandNameAlreadyExistsException("El nombre de la marca ya existe");
    }

    public BrandResponseDTO findCanchaBrandById(Long id) throws BrandNotFoundException {
        return brandMapper.toDto(
                findBrandOrThrow(id)
        );
    }

    public List<BrandResponseDTO> getBrandsByOwnerId(UUID id) throws UserNotFoundException {
        ownerService.findOwnerOrThrow(id);
        return brandMapper.toDto(
                canchaBrandRepository.findByOwnerIdAndActive(id, true)
        );
    }

    public List<CanchaResponseDTO> getCanchasByBrandId(Long brandId) {
        return canchaMapper.toDto(
                canchaRepository.findByEstablishmentId(brandId)
        );
    }
}
