package com.example.CanchaSystem.Mapper;

import com.example.CanchaSystem.dto.request.AdminRequestDTO;
import com.example.CanchaSystem.dto.response.AdminResponseDTO;
import com.example.CanchaSystem.model.Admin;
import com.example.CanchaSystem.model.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminMapper {
    Admin toEntity(AdminRequestDTO dto);
    AdminResponseDTO toDto(Admin entity);

    default String map(Role role) {
        return role != null ? role.toString() : null;
    }
}
