package com.example.CanchaSystem.service;

import com.example.CanchaSystem.Mapper.AddressMapper;
import com.example.CanchaSystem.dto.AddressDTO;
import com.example.CanchaSystem.exception.address.AddressNotFoundException;
import com.example.CanchaSystem.model.Address;
import com.example.CanchaSystem.repository.AddressRepository;
import org.springframework.stereotype.Service;

@Service
public class AddressService {
    private final AddressMapper addressMapper;
    private final AddressRepository addressRepository;

    public AddressService(AddressMapper addressMapper, AddressRepository addressRepository) {
        this.addressMapper = addressMapper;
        this.addressRepository = addressRepository;
    }

    public Address insertAddress(AddressDTO addressDTO) {
        Address address = addressMapper.toEntity(addressDTO);
        return addressRepository.save(address);
    }

    public Address findAddressByIdOrThrow(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new AddressNotFoundException(id));
    }

    public void deleteAddressById(Long id) {
        addressRepository.deleteById(id);
    }
}
