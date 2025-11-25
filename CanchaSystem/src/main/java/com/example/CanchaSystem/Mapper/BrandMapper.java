package com.example.CanchaSystem.Mapper;

import com.example.CanchaSystem.dto.request.BrandRequestDTO;
import com.example.CanchaSystem.dto.response.BrandResponseDTO;
import com.example.CanchaSystem.model.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    @Mapping(target = "owner.id", source = "ownerId")
    Brand toEntity(BrandRequestDTO dto);

    @Mapping(source = "owner.id", target = "ownerId")
    BrandResponseDTO toDto(Brand entity);

    @Mapping(source = "owner.id", target = "ownerId")
    List<BrandResponseDTO> toDto(List<Brand> entities);
}
