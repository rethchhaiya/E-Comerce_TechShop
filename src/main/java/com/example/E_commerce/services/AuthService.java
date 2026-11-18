package com.example.E_commerce.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.E_commerce.dto.requests.CustomerRequest;
import com.example.E_commerce.dto.requests.LoginRequest;
import com.example.E_commerce.dto.responses.CustomerResponse;
import com.example.E_commerce.dto.responses.TokenResponse;
import com.example.E_commerce.exception.InvalidCredentialsException;
import com.example.E_commerce.models.Customer;
import com.example.E_commerce.repository.CustomerRepository;
import com.example.E_commerce.security.JwtService;

@Service
public class AuthService {

    private final CustomerRepository customerReposity;
    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(CustomerRepository customerReposity, CustomerService customerService, PasswordEncoder passwordEncoder, JwtService jwtService){
        this.customerReposity = customerReposity;
        this.customerService = customerService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public CustomerResponse register(CustomerRequest request){
        return customerService.create(request);
    }

    public TokenResponse login(LoginRequest request){
        Customer customer = customerReposity.findByEmail(request.getEmail())
                                             .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if(!passwordEncoder.matches(request.getPassword(), customer.getPasswordHash())){
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return jwtService.generateToken(customer);
    }
}
