package com.example.E_commerce.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.E_commerce.models.Customer;

/**
 * CustomerReposity
 */
public interface CustomerRepository extends JpaRepository<Customer, Long>{
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
    Optional<Customer> findByEmail(String email);
}
