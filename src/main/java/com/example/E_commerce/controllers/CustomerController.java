package com.example.E_commerce.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.example.E_commerce.dto.requests.CustomerRequest;
import com.example.E_commerce.dto.requests.RoleRequest;
import com.example.E_commerce.dto.responses.CustomerResponse;
import com.example.E_commerce.services.CustomerService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/customer")
@Tag(name = "Customers", description = "Customer profiles and roles")
public class CustomerController {
    
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService){
        this.customerService = customerService;
    }
    
    @PostMapping 
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.create(request));
    }

    @GetMapping 
    @PreAuthorize("hasRole('ADMIN')")
    public List<CustomerResponse> getAll(){
        return customerService.getAll();
    }

    @GetMapping("/me")
    public CustomerResponse getMe(){
        return customerService.getMe();
    }

    @PutMapping("/me")
    public CustomerResponse updateMe(@Valid @RequestBody CustomerRequest request){
        return customerService.updateMe(request);
    }

    @GetMapping("/{id}")
    public CustomerResponse getById(@PathVariable Long id){
        return customerService.getById(id);
    }

    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request){
        return customerService.update(id, request);
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public CustomerResponse changeRole(@PathVariable Long id, @Valid @RequestBody RoleRequest request){
        return customerService.changeRole(id, request.getRole());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id){
        customerService.delete(id);
    }
}
