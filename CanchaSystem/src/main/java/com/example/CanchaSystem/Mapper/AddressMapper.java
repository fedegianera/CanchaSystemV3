package com.example.CanchaSystem.Mapper;

import com.example.CanchaSystem.dto.AddressDTO;
import com.example.CanchaSystem.model.Address;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address toEntity(AddressDTO dto);

    AddressDTO toDto(Address entity);
    List<AddressDTO> toDto(List<Address> entities);
}
