package com.example.E_commerce.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    private Long id;
    private Long customerId;
    private String recipientName;
    private String phone;
    private String addressLine;
    private String city;
    private String province;
    private Boolean isDefault;
}
