package com.example.E_commerce.mappers;

import org.springframework.stereotype.Component;

import com.example.E_commerce.dto.responses.CustomerResponse;
import com.example.E_commerce.models.Customer;

@Component 
public class CustomerMapper {
    
    public CustomerResponse toResponse(Customer customer){

        CustomerResponse response = new CustomerResponse(
            customer.getId(),
            customer.getUserName(),
            customer.getEmail(),
            customer.getCreatedAt()
        );
        return response;

    }
}
