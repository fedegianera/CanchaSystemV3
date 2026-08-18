package com.example.CanchaSystem.Mapper;

import com.example.CanchaSystem.dto.request.EstablishmentRequestDTO;
import com.example.CanchaSystem.dto.response.EstablishmentResponseDTO;
import com.example.CanchaSystem.model.Establishment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EstablishmentMapper {

    @Mapping(target = "brand.id", source = "brandId")
    Establishment toEntity(EstablishmentRequestDTO dto);

    @Mapping(source = "brand.id", target = "brandId")
    EstablishmentResponseDTO toDto(Establishment entity);

    @Mapping(source = "brand.id", target = "brandId")
    List<EstablishmentResponseDTO> toDto(List<Establishment> entities);
}
