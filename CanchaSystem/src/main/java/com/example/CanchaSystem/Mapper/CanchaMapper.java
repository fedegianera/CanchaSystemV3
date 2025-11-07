package com.example.CanchaSystem.Mapper;

import com.example.CanchaSystem.dto.request.CanchaRequestDTO;
import com.example.CanchaSystem.dto.response.CanchaResponseDTO;
import com.example.CanchaSystem.model.Cancha;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CanchaMapper {
    Cancha toEntity(CanchaRequestDTO dto);
    CanchaResponseDTO toDto(Cancha entity);
    List<CanchaResponseDTO> toDto(List<Cancha> entities);
}
