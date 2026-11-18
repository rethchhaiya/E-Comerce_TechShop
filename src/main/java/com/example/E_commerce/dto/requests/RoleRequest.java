package com.example.E_commerce.dto.requests;

import com.example.E_commerce.models.Role;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleRequest {

    @NotNull
    private Role role;
}
