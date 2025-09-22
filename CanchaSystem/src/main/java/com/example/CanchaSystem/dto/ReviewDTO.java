package com.example.CanchaSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
    private ClientDTO clientDTO;
    private CanchaDTO canchaDTO;
    private double rating;
    private String message;
}
