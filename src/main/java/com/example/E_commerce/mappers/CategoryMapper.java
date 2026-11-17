package com.example.E_commerce.mappers;

import org.springframework.stereotype.Component;

import com.example.E_commerce.dto.responses.CategoryResponse;
import com.example.E_commerce.models.Category;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category){
        return new CategoryResponse(
            category.getId(),
            category.getParentId(),
            category.getName(),
            category.getSlug()
        );
    }
}
