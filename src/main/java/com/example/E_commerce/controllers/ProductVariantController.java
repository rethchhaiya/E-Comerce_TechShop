package com.example.E_commerce.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.E_commerce.dto.requests.ProductVariantRequest;
import com.example.E_commerce.dto.responses.ProductVariantResponse;
import com.example.E_commerce.services.ProductVariantService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/product-variants")
@Tag(name = "Product-Variants")
public class ProductVariantController {
    private final ProductVariantService productVariantService;

    public ProductVariantController(ProductVariantService productVariantService){
        this.productVariantService = productVariantService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductVariantResponse> create(@Valid @RequestBody ProductVariantRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(productVariantService.create(request));
    }

    @GetMapping 
    public List<ProductVariantResponse> getAll(){
        return productVariantService.getAll();
    }
}
