package com.example.CanchaSystem.Mapper;

import com.example.CanchaSystem.dto.request.EstablishmentRequestDTO;
import com.example.CanchaSystem.dto.response.EstablishmentResponseDTO;
import com.example.CanchaSystem.model.Establishment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EstablishmentMapper {
    Establishment toEntity(EstablishmentRequestDTO dto);
    EstablishmentResponseDTO toDto(Establishment entity);
    List<EstablishmentResponseDTO> toDto(List<Establishment> entities);
}
