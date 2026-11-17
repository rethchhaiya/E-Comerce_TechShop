package com.example.E_commerce.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.E_commerce.dto.requests.CustomerRequest;
import com.example.E_commerce.dto.responses.CustomerResponse;
import com.example.E_commerce.exception.DuplicateEmailException;
import com.example.E_commerce.exception.ResourceNotFoundException;
import com.example.E_commerce.mappers.CustomerMapper;
import com.example.E_commerce.models.Customer;
import com.example.E_commerce.repository.CustomerReposity;

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
        customer.setPhone(request.getPhone());
        customer.setPasswordHash(request.getPasswordHash());
        customer.setCreatedAt(LocalDateTime.now());

        Customer saved = customerReposity.save(customer);

        return customerMapper.toResponse(saved);
    }

    public List<CustomerResponse> getAll(){
        return customerReposity.findAll().stream().map(customerMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional 
    public CustomerResponse getById(Long id){
        Customer customer = getEntity(id);

        return customerMapper.toResponse(customer);
    }

    public CustomerResponse update(Long id, CustomerRequest request){

        Customer customer = getEntity(id);

        customer.setUserName(request.getUserName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setPasswordHash(request.getPasswordHash());

        Customer saved = customerReposity.save(customer);

        return customerMapper.toResponse(saved);
        
    }

    public void delete(Long id){
        Customer customer = getEntity(id);

        customerReposity.delete(customer);
    }

    private Customer getEntity(Long id){
        return customerReposity.findById(id)
                               .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

}
