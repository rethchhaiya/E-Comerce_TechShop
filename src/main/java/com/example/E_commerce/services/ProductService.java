package com.example.E_commerce.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.E_commerce.dto.requests.ProductRequest;
import com.example.E_commerce.dto.responses.ProductResponse;
import com.example.E_commerce.exception.DuplicateProductNameException;
import com.example.E_commerce.exception.InvalidSortException;
import com.example.E_commerce.exception.ResourceNotFoundException;
import com.example.E_commerce.mappers.ProductMapper;
import com.example.E_commerce.models.Product;
import com.example.E_commerce.repository.CategoryRepository;
import com.example.E_commerce.repository.ProductRepository;

@Service
public class ProductService {

    private static final Map<String, String> SORT_FIELDS = Map.of(
        "id", "id",
        "name", "name",
        "createdAt", "createdAt",
        "isActive", "isActive"
    );

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, ProductMapper productMapper){
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    public ProductResponse create(ProductRequest request){
        validateCategory(request.getCategoryId());

        if(productRepository.existsByNameAndCategoryId(request.getName(), request.getCategoryId())){
            throw new DuplicateProductNameException("Product name already exists in this category: " + request.getName());
        }

        Product product = new Product();
        product.setCategoryId(request.getCategoryId());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setIsActive(request.getIsActive() == null ? true : request.getIsActive());
        product.setCreatedAt(LocalDateTime.now());

        Product saved = productRepository.save(product);

        return productMapper.toResponse(saved);
    }

    public List<ProductResponse> getAll(String sortBy, String direction, Boolean active){
        Sort sort = buildSort(sortBy, direction);

        List<Product> products;
        if(active == null){
            products = productRepository.findAll(sort);
        } else {
            products = productRepository.findByIsActive(active, sort);
        }

        return products.stream().map(productMapper::toResponse).collect(Collectors.toList());
    }

    public ProductResponse getById(Long id){
        return productMapper.toResponse(getEntity(id));
    }

    public ProductResponse update(Long id, ProductRequest request){

        Product product = getEntity(id);
        validateCategory(request.getCategoryId());

        if(productRepository.existsByNameAndCategoryIdAndIdNot(request.getName(), request.getCategoryId(), id)){
            throw new DuplicateProductNameException("Product name already exists in this category: " + request.getName());
        }

        product.setCategoryId(request.getCategoryId());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        if(request.getIsActive() != null){
            product.setIsActive(request.getIsActive());
        }

        Product saved = productRepository.save(product);

        return productMapper.toResponse(saved);
    }

    public void delete(Long id){
        productRepository.delete(getEntity(id));
    }

    private Product getEntity(Long id){
        return productRepository.findById(id)
                                 .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private void validateCategory(Long categoryId){
        if(!categoryRepository.existsById(categoryId)){
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
    }

    private Sort buildSort(String sortBy, String direction){
        String field = SORT_FIELDS.get(sortBy);
        if(field == null){
            throw new InvalidSortException("Invalid sort field: " + sortBy + ". Allowed values: id, name, createdAt, isActive");
        }

        Sort.Direction sortDirection;
        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException exception){
            throw new InvalidSortException("Invalid sort direction: " + direction + ". Allowed values: asc, desc");
        }

        return Sort.by(sortDirection, field);
    }
}
