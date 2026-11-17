package com.example.E_commerce.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.E_commerce.dto.requests.AddressRequest;
import com.example.E_commerce.dto.responses.AddressResponse;
import com.example.E_commerce.exception.ResourceNotFoundException;
import com.example.E_commerce.mappers.AddressMapper;
import com.example.E_commerce.models.Address;
import com.example.E_commerce.repository.AddressRepository;
import com.example.E_commerce.repository.CustomerReposity;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CustomerReposity customerReposity;

    private final AddressMapper addressMapper = new AddressMapper();

    private AddressService addressService;

    @BeforeEach
    void setUp(){
        addressService = new AddressService(addressRepository, customerReposity, addressMapper);
    }

    @Test
    void createSavesAddressForExistingCustomer(){
        when(customerReposity.existsById(1L)).thenReturn(true);
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address address = invocation.getArgument(0);
            address.setId(1L);
            return address;
        });

        AddressResponse response = addressService.create(request(false));

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCustomerId()).isEqualTo(1L);
        assertThat(response.getRecipientName()).isEqualTo("Reth Chhaiya");
        assertThat(response.getIsDefault()).isFalse();
        verify(addressRepository, never()).clearDefaultByCustomerId(anyLong());
    }

    @Test
    void createDefaultClearsPreviousDefault(){
        when(customerReposity.existsById(1L)).thenReturn(true);
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address address = invocation.getArgument(0);
            address.setId(1L);
            return address;
        });

        AddressResponse response = addressService.create(request(true));

        assertThat(response.getIsDefault()).isTrue();
        verify(addressRepository).clearDefaultByCustomerId(1L);
    }

    @Test
    void createRejectsUnknownCustomer(){
        AddressRequest request = request(false);
        request.setCustomerId(99L);
        when(customerReposity.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> addressService.create(request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Customer not found");

        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void updateKeepsCurrentDefaultWhenOmitted(){
        Address existing = new Address();
        existing.setId(5L);
        existing.setCustomerId(1L);
        existing.setRecipientName("Reth Chhaiya");
        existing.setPhone("012345678");
        existing.setAddressLine("Street 1");
        existing.setCity("Phnom Penh");
        existing.setProvince("Phnom Penh");
        existing.setIsDefault(true);
        when(addressRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(customerReposity.existsById(1L)).thenReturn(true);
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AddressResponse response = addressService.update(5L, request(null));

        assertThat(response.getIsDefault()).isTrue();
        verify(addressRepository).clearDefaultByCustomerIdExcluding(1L, 5L);
    }

    @Test
    void getByIdRejectsUnknownAddress(){
        when(addressRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.getById(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Address not found with id: 99");
    }

    private AddressRequest request(Boolean isDefault){
        AddressRequest request = new AddressRequest();
        request.setCustomerId(1L);
        request.setRecipientName("Reth Chhaiya");
        request.setPhone("012345678");
        request.setAddressLine("Street 1");
        request.setCity("Phnom Penh");
        request.setProvince("Phnom Penh");
        request.setIsDefault(isDefault);
        return request;
    }
}
