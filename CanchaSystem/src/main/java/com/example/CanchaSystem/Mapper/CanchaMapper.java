package com.example.CanchaSystem.Mapper;

import com.example.CanchaSystem.dto.request.CanchaRequestDTO;
import com.example.CanchaSystem.dto.response.CanchaResponseDTO;
import com.example.CanchaSystem.model.Cancha;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CanchaMapper {
    @Mapping(target = "establishment.id", source = "establishmentId")
    Cancha toEntity(CanchaRequestDTO dto);

    @Mapping(source = "establishment.id", target = "establishmentId")
    CanchaResponseDTO toDto(Cancha entity);

    @Mapping(source = "establishment.id", target = "establishmentId")
    List<CanchaResponseDTO> toDto(List<Cancha> entities);
}
