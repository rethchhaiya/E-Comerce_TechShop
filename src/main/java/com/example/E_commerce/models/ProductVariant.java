package com.example.E_commerce.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "product_variants")
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "product_id")
    private Long productId;

    @Column(nullable = false, unique = true, name = "sku")
    private String sku;

    @Column(nullable = false, name = "variant_name")
    private String variantName;

    @Column(nullable = false, precision = 10, scale = 2, name = "price")
    private BigDecimal price;

    @Column(nullable = false, precision = 10, scale = 2, name = "cost")
    private BigDecimal cost;

    @Column(nullable = false, name = "stock_quantity")
    private Integer stockQuantity;

    @Column(nullable = false, name = "is_active")
    private Boolean isActive;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;
}
