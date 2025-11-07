package com.example.CanchaSystem.Mapper;

import com.example.CanchaSystem.dto.request.OwnerRequestDTO;
import com.example.CanchaSystem.dto.response.OwnerResponseDTO;
import com.example.CanchaSystem.model.Owner;
import com.example.CanchaSystem.model.Role;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OwnerMapper {
    Owner toEntity(OwnerRequestDTO dto);
    OwnerResponseDTO toDto(Owner entity);

    List<OwnerResponseDTO> toDto(List<Owner> entities);

    default String map(Role role) {
        return role != null ? role.getName() : null;
    }
}
