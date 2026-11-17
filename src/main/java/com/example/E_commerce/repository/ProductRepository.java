package com.example.E_commerce.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.E_commerce.models.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>{
    boolean existsByCategoryId(Long categoryId);
    boolean existsByNameAndCategoryId(String name, Long categoryId);
    boolean existsByNameAndCategoryIdAndIdNot(String name, Long categoryId, Long id);
    List<Product> findByIsActive(Boolean isActive, Sort sort);
}
