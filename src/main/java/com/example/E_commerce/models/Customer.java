package com.example.E_commerce.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@Entity 
@NoArgsConstructor 
@Table(name = "customers")
public class Customer {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String userName;

    @Column(unique = true, name = "email")
    private String email;

    @Column(name = "passwordHash")
    private String passwordHash;

    @Column(name = "phone")
    private String phone;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "role")
    private Role role;
}
