package com.example.E_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.E_commerce.models.ProductVariant;

/**
 * ProductVariantRepository
 */
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long>{
    boolean existsBySku(String sku);
}
