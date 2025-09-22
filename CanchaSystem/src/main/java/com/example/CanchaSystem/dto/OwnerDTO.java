package com.example.CanchaSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OwnerDTO {
    private String name;
    private String lastName;
    private String username;
    private String mail;
    private String cellNumber;
    private String role;
}
