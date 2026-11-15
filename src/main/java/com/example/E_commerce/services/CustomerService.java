package com.example.E_commerce.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.E_commerce.dto.requests.CustomerRequest;
import com.example.E_commerce.dto.responses.CustomerResponse;
import com.example.E_commerce.exception.DuplicateEmailException;
import com.example.E_commerce.mappers.CustomerMapper;
import com.example.E_commerce.models.Customer;
import com.example.E_commerce.repository.CustomerReposity;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service 
public class CustomerService {

    private final CustomerReposity customerReposity;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerReposity customerReposity, CustomerMapper customerMapper){
        this.customerReposity = customerReposity;
        this.customerMapper = customerMapper;
    }

    public CustomerResponse create(CustomerRequest request){
        if(customerReposity.existsByEmail(request.getEmail())){
            throw new DuplicateEmailException("Email already exist");
        }

        Customer customer = new Customer();

        customer.setUserName(request.getUserName());
        customer.setEmail(request.getEmail());
        customer.setPasswordHash(request.getPasswordHash());

        Customer saved = customerReposity.save(customer);

        return customerMapper.toResponse(saved);
    }

    public List<CustomerResponse> getAll(){
        return customerReposity.findAll().stream().map(customerMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional 
    public CustomerResponse getById(Long id){
        return customerMapper.toResponse(getEntity(id));
    }

    private Customer getEntity(Long id){
        return customerReposity.findById(id)
                               .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
    }

}
