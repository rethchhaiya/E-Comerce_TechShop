package com.example.E_commerce.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.E_commerce.models.Customer;
import com.example.E_commerce.models.Role;
import com.example.E_commerce.repository.CustomerRepository;

@Component
public class CurrentUserService {

    private final CustomerRepository customerRepository;

    public CurrentUserService(CustomerRepository customerReposity){
        this.customerRepository = customerReposity;
    }

    public Customer getCurrentCustomer(){
        //this happend when user try to make a request
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken){
            throw new AccessDeniedException("Authentication required");
        }

        return customerRepository.findByEmail(authentication.getName())
                               .orElseThrow(() -> new AccessDeniedException("Authenticated customer no longer exists"));
    }

    public boolean isAdmin(Customer customer){
        return customer.getRole() == Role.ADMIN;
    }
}
