package com.example.CanchaSystem.Mapper;

import com.example.CanchaSystem.dto.request.ClientRequestDTO;
import com.example.CanchaSystem.dto.response.ClientResponseDTO;
import com.example.CanchaSystem.model.Client;
import com.example.CanchaSystem.model.Role;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    Client toEntity(ClientRequestDTO dto);
    ClientResponseDTO toDto(Client entity);

    List<ClientResponseDTO> toDto(List<Client> entities);

    default String map(Role role) {
        return role != null ? role.toString() : null;
    }

}
