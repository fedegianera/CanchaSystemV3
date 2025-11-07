package com.example.CanchaSystem.Mapper;

import com.example.CanchaSystem.dto.request.BrandRequestDTO;
import com.example.CanchaSystem.dto.response.BrandResponseDTO;
import com.example.CanchaSystem.model.Brand;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    Brand toEntity(BrandRequestDTO dto);
    BrandResponseDTO toDto(Brand entity);
    List<BrandResponseDTO> toDto(List<Brand> entities);
}
