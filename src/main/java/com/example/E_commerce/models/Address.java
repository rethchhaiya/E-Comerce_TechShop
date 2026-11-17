package com.example.E_commerce.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "customer_id")
    private Long customerId;

    @Column(nullable = false, name = "recipient_name")
    private String recipientName;

    @Column(nullable = false, name = "phone")
    private String phone;

    @Column(nullable = false, name = "address_line")
    private String addressLine;

    @Column(nullable = false, name = "city")
    private String city;

    @Column(nullable = false, name = "province")
    private String province;

    @Column(nullable = false, name = "is_default")
    private Boolean isDefault;
}
