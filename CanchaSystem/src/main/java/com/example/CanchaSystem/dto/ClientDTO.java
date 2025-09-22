package com.example.CanchaSystem.dto;

import com.example.CanchaSystem.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClientDTO {
    private String name;
    private String lastName;
    private String username;
    private String mail;
    private String cellNumber;
    private String role;
}
