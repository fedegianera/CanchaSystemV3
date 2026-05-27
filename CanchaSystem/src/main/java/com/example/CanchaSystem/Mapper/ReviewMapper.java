package com.example.CanchaSystem.Mapper;

import com.example.CanchaSystem.dto.request.ReviewRequestDTO;
import com.example.CanchaSystem.dto.response.ReviewResponseDTO;
import com.example.CanchaSystem.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(source = "clientId", target = "client.id")
    @Mapping(source = "clientName", target = "client.name")
    @Mapping(source = "establishmentId", target = "establishment.id")
    Review toEntity(ReviewRequestDTO dto);

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.name", target = "clientName")
    @Mapping(source = "establishment.id", target = "establishmentId")
    ReviewResponseDTO toDto(Review entity);

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.name", target = "clientName")
    @Mapping(source = "establishment.id", target = "establishmentId")
    List<ReviewResponseDTO> toDto(List<Review> entities);
}
