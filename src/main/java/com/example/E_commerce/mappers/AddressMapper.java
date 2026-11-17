package com.example.E_commerce.mappers;

import org.springframework.stereotype.Component;

import com.example.E_commerce.dto.responses.AddressResponse;
import com.example.E_commerce.models.Address;

@Component
public class AddressMapper {

    public AddressResponse toResponse(Address address){
        return new AddressResponse(
            address.getId(),
            address.getCustomerId(),
            address.getRecipientName(),
            address.getPhone(),
            address.getAddressLine(),
            address.getCity(),
            address.getProvince(),
            address.getIsDefault()
        );
    }
}
