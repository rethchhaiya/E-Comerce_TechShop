package com.example.E_commerce.dto.requests;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductVariantRequest {

    @NotNull
    private Long productId;

    @NotBlank
    @Size(max = 100)
    private String sku;

    @NotBlank
    @Size(max = 255)
    private String variantName;

    @NotNull
    @DecimalMin(value = "0.00")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal price;

    @NotNull
    @DecimalMin(value = "0.00")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal cost;

    @Min(0)
    private Integer stockQuantity;

    private Boolean isActive;
}
