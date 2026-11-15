package com.example.E_commerce.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.E_commerce.models.Customer;

/**
 * CustomerReposity
 */
public interface CustomerReposity extends JpaRepository<Customer, Long>{
    boolean existsByEmail(String email);
}
