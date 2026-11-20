package com.example.E_commerce.mappers;

import org.springframework.stereotype.Component;

import com.example.E_commerce.dto.responses.ProductVariantResponse;
import com.example.E_commerce.models.ProductVariant;

@Component 
public class ProductVariantMapper {
    public ProductVariantResponse toResponse(ProductVariant variant){
        return new ProductVariantResponse(
            variant.getId(),
            variant.getProductId(),
            variant.getSku(),
            variant.getVariantName(),
            variant.getPrice(),
            variant.getCost(),
            variant.getStockQuantity(),
            variant.getIsActive(),
            variant.getCreatedAt()
        );
    }
}
