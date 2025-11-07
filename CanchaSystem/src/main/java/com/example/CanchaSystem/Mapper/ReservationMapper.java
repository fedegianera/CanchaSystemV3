package com.example.CanchaSystem.Mapper;

import com.example.CanchaSystem.dto.request.ReservationRequestDTO;
import com.example.CanchaSystem.dto.response.ReservationResponseDTO;
import com.example.CanchaSystem.model.Reservation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    Reservation toEntity(ReservationRequestDTO dto);
    ReservationResponseDTO toDto(Reservation entity);
    List<ReservationResponseDTO> toDto(List<Reservation> entities);
}
