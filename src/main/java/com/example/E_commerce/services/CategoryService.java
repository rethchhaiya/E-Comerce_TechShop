package com.example.E_commerce.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.E_commerce.dto.requests.CategoryRequest;
import com.example.E_commerce.dto.responses.CategoryResponse;
import com.example.E_commerce.exception.DuplicateSlugException;
import com.example.E_commerce.exception.InvalidParentCategoryException;
import com.example.E_commerce.exception.ResourceNotFoundException;
import com.example.E_commerce.mappers.CategoryMapper;
import com.example.E_commerce.models.Category;
import com.example.E_commerce.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper){
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public CategoryResponse create(CategoryRequest request){
        if(categoryRepository.existsBySlug(request.getSlug())){
            throw new DuplicateSlugException("Category slug already exists: " + request.getSlug());
        }
        validateParent(request.getParentId(), null);

        Category category = new Category();
        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setParentId(request.getParentId());

        Category saved = categoryRepository.save(category);

        return categoryMapper.toResponse(saved);
    }

    public List<CategoryResponse> getAll(){
        return categoryRepository.findAll().stream().map(categoryMapper::toResponse).collect(Collectors.toList());
    }

    public CategoryResponse getById(Long id){
        return categoryMapper.toResponse(getEntity(id));
    }

    public CategoryResponse update(Long id, CategoryRequest request){

        Category category = getEntity(id);

        if(categoryRepository.existsBySlugAndIdNot(request.getSlug(), id)){
            throw new DuplicateSlugException("Category slug already exists: " + request.getSlug());
        }
        validateParent(request.getParentId(), id);

        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setParentId(request.getParentId());

        Category saved = categoryRepository.save(category);

        return categoryMapper.toResponse(saved);
    }

    public void delete(Long id){
        categoryRepository.delete(getEntity(id));
    }

    private Category getEntity(Long id){
        return categoryRepository.findById(id)
                                  .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    private void validateParent(Long parentId, Long categoryId){
        if(parentId == null){
            return;
        }
        if(parentId.equals(categoryId)){
            throw new InvalidParentCategoryException("A category cannot be its own parent");
        }
        if(!categoryRepository.existsById(parentId)){
            throw new ResourceNotFoundException("Parent category not found with id: " + parentId);
        }
    }
}
