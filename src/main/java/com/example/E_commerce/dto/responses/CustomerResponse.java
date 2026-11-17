package com.example.E_commerce.dto.responses;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class CustomerResponse {
    private Long id;
    private String userName;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
}
