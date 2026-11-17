package com.example.E_commerce.mappers;

import org.springframework.stereotype.Component;

import com.example.E_commerce.dto.responses.ProductResponse;
import com.example.E_commerce.models.Product;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product){
        return new ProductResponse(
            product.getId(),
            product.getCategoryId(),
            product.getName(),
            product.getDescription(),
            product.getIsActive(),
            product.getCreatedAt()
        );
    }
}
