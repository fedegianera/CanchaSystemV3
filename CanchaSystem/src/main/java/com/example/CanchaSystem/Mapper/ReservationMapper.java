package com.example.CanchaSystem.Mapper;

import com.example.CanchaSystem.dto.request.ReservationRequestDTO;
import com.example.CanchaSystem.dto.response.ReservationResponseDTO;
import com.example.CanchaSystem.model.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    @Mapping(target = "cancha.establishment.id", source = "establishmentId")
    Reservation toEntity(ReservationRequestDTO dto);

    @Mapping(source = "cancha.id", target = "canchaId")
    @Mapping(source = "cancha.establishment.id", target = "establishmentId")
    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "cancha.canchaType", target = "canchaType")
    ReservationResponseDTO toDto(Reservation entity);

    @Mapping(source = "cancha.id", target = "canchaId")
    @Mapping(source = "cancha.establishment.id", target = "establishmentId")
    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "cancha.canchaType", target = "canchaType")
    List<ReservationResponseDTO> toDto(List<Reservation> entities);
}
