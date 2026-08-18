package com.example.CanchaSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EstablishmentRatingDTO {
    private Long establishmentId;
    private double averageRating;
}
