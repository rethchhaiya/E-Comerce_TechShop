package com.example.E_commerce.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
public class CustomerRequest {
    
    @NotBlank
    @Size(max = 100)
    private String userName;

    @NotBlank 
    @Email 
    private String email;

    @NotBlank
    @Size(max = 20)
    private String phone;

    @NotBlank 
    @Size(min = 8, max = 100)
    private String passwordHash;
}
