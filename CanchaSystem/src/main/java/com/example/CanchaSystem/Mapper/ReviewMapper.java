package com.example.CanchaSystem.Mapper;

import com.example.CanchaSystem.dto.request.ReviewRequestDTO;
import com.example.CanchaSystem.dto.response.ReviewResponseDTO;
import com.example.CanchaSystem.model.Review;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    Review toEntity(ReviewRequestDTO dto);
    ReviewResponseDTO toDto(Review entity);
    List<ReviewResponseDTO> toDto(List<Review> entities);
}
