package com.example.E_commerce.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.E_commerce.dto.requests.ProductVariantRequest;
import com.example.E_commerce.dto.responses.ProductVariantResponse;
import com.example.E_commerce.exception.DuplicateSkuException;
import com.example.E_commerce.exception.ResourceNotFoundException;
import com.example.E_commerce.mappers.ProductVariantMapper;
import com.example.E_commerce.models.ProductVariant;
import com.example.E_commerce.repository.ProductVariantRepository;


@Service 
public class ProductVariantService {

    private final ProductVariantMapper productVariantMapper;
    private final ProductVariantRepository productVariantRepository;

    public ProductVariantService(ProductVariantMapper productVariantMapper, ProductVariantRepository productVariantRepository){
        this.productVariantMapper = productVariantMapper;
        this.productVariantRepository = productVariantRepository;
    }

    public ProductVariantResponse create(ProductVariantRequest request){

        //check if productId is exists or not
        validate(request);

        ProductVariant variant = new ProductVariant();
        variant.setProductId(request.getProductId());
        variant.setSku(request.getSku());
        variant.setVariantName(request.getVariantName());
        variant.setPrice(request.getPrice());
        variant.setCost(request.getCost());
        variant.setStockQuantity(request.getStockQuantity());
        variant.setIsActive(request.getIsActive());
        variant.setCreatedAt(LocalDateTime.now());

        ProductVariant saved = productVariantRepository.save(variant);
        return productVariantMapper.toResponse(saved);
    }

    public List<ProductVariantResponse> getAll(){
        return productVariantRepository.findAll().stream()
                                       .map(productVariantMapper::toResponse).collect(Collectors.toList());
    }

    private void validate(ProductVariantRequest request){
        if(productVariantRepository.existsBySku(request.getSku())){
            throw new DuplicateSkuException("SKU already exists");
        }
        if(productVariantRepository.existsById(request.getProductId())){
            throw new ResourceNotFoundException("Product not found with id: " + request.getProductId());
        }
    }
}
