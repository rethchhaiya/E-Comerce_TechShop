package com.example.E_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.E_commerce.models.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{
    boolean existsBySlug(String slug);
    boolean existsBySlugAndIdNot(String slug, Long id);
}
