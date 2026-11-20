package com.example.E_commerce.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantResponse {

    private Long id;
    private Long productId;
    private String sku;
    private String variantName;
    private BigDecimal price;
    private BigDecimal cost;
    private Integer stockQuantity;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
