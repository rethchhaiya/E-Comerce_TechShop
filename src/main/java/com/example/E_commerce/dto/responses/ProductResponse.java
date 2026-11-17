package com.example.E_commerce.dto.responses;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private Long categoryId;
    private String name;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
