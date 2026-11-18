package com.example.E_commerce.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.example.E_commerce.dto.requests.AddressRequest;
import com.example.E_commerce.dto.responses.AddressResponse;
import com.example.E_commerce.exception.ResourceNotFoundException;
import com.example.E_commerce.mappers.AddressMapper;
import com.example.E_commerce.models.Address;
import com.example.E_commerce.models.Customer;
import com.example.E_commerce.models.Role;
import com.example.E_commerce.repository.AddressRepository;
import com.example.E_commerce.repository.CustomerRepository;
import com.example.E_commerce.security.CurrentUserService;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CustomerRepository customerReposity;

    @Mock
    private CurrentUserService currentUserService;

    private final AddressMapper addressMapper = new AddressMapper();

    private AddressService addressService;

    @BeforeEach
    void setUp(){
        addressService = new AddressService(addressRepository, customerReposity, addressMapper, currentUserService);
    }

    @Test
    void createSavesAddressForExistingCustomer(){
        Customer user = customer(1L, Role.USER);
        when(currentUserService.getCurrentCustomer()).thenReturn(user);
        when(currentUserService.isAdmin(user)).thenReturn(false);
        when(customerReposity.existsById(1L)).thenReturn(true);
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address address = invocation.getArgument(0);
            address.setId(1L);
            return address;
        });

        AddressResponse response = addressService.create(request(1L, false));

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCustomerId()).isEqualTo(1L);
        assertThat(response.getRecipientName()).isEqualTo("Reth Chhaiya");
        assertThat(response.getIsDefault()).isFalse();
        verify(addressRepository, never()).clearDefaultByCustomerId(anyLong());
    }

    @Test
    void createDefaultClearsPreviousDefault(){
        Customer user = customer(1L, Role.USER);
        when(currentUserService.getCurrentCustomer()).thenReturn(user);
        when(currentUserService.isAdmin(user)).thenReturn(false);
        when(customerReposity.existsById(1L)).thenReturn(true);
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address address = invocation.getArgument(0);
            address.setId(1L);
            return address;
        });

        AddressResponse response = addressService.create(request(1L, true));

        assertThat(response.getIsDefault()).isTrue();
        verify(addressRepository).clearDefaultByCustomerId(1L);
    }

    @Test
    void createRejectsAddressForAnotherCustomer(){
        Customer user = customer(1L, Role.USER);
        when(currentUserService.getCurrentCustomer()).thenReturn(user);
        when(currentUserService.isAdmin(user)).thenReturn(false);

        assertThatThrownBy(() -> addressService.create(request(2L, false)))
            .isInstanceOf(AccessDeniedException.class)
            .hasMessage("You can only manage your own addresses");

        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void createRejectsUnknownCustomerForAdmin(){
        Customer admin = customer(9L, Role.ADMIN);
        when(currentUserService.getCurrentCustomer()).thenReturn(admin);
        when(currentUserService.isAdmin(admin)).thenReturn(true);
        when(customerReposity.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> addressService.create(request(99L, false)))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Customer not found");

        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void updateKeepsCurrentDefaultWhenOmitted(){
        Address existing = address(5L, 1L, true);
        Customer user = customer(1L, Role.USER);
        when(addressRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(currentUserService.getCurrentCustomer()).thenReturn(user);
        when(currentUserService.isAdmin(user)).thenReturn(false);
        when(customerReposity.existsById(1L)).thenReturn(true);
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AddressResponse response = addressService.update(5L, request(1L, null));

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

    @Test
    void getByIdHidesAnotherCustomersAddress(){
        Address existing = address(5L, 1L, false);
        Customer other = customer(2L, Role.USER);
        when(addressRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(currentUserService.getCurrentCustomer()).thenReturn(other);
        when(currentUserService.isAdmin(other)).thenReturn(false);

        assertThatThrownBy(() -> addressService.getById(5L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Address not found with id: 5");
    }

    @Test
    void deleteDefaultPromotesAnotherAddress(){
        Address existing = address(10L, 1L, true);
        Address remaining = address(11L, 1L, false);
        Customer user = customer(1L, Role.USER);
        when(addressRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(currentUserService.getCurrentCustomer()).thenReturn(user);
        when(currentUserService.isAdmin(user)).thenReturn(false);
        when(addressRepository.findByCustomerIdOrderByIdAsc(1L)).thenReturn(List.of(remaining));

        addressService.delete(10L);

        verify(addressRepository).delete(existing);
        verify(addressRepository).flush();
        assertThat(remaining.getIsDefault()).isTrue();
        verify(addressRepository).save(remaining);
    }

    private Customer customer(Long id, Role role){
        Customer customer = new Customer();
        customer.setId(id);
        customer.setEmail(id + "@example.com");
        customer.setRole(role);
        return customer;
    }

    private Address address(Long id, Long customerId, Boolean isDefault){
        Address address = new Address();
        address.setId(id);
        address.setCustomerId(customerId);
        address.setRecipientName("Reth Chhaiya");
        address.setPhone("012345678");
        address.setAddressLine("Street 1");
        address.setCity("Phnom Penh");
        address.setProvince("Phnom Penh");
        address.setIsDefault(isDefault);
        return address;
    }

    private AddressRequest request(Long customerId, Boolean isDefault){
        AddressRequest request = new AddressRequest();
        request.setCustomerId(customerId);
        request.setRecipientName("Reth Chhaiya");
        request.setPhone("012345678");
        request.setAddressLine("Street 1");
        request.setCity("Phnom Penh");
        request.setProvince("Phnom Penh");
        request.setIsDefault(isDefault);
        return request;
    }
}
