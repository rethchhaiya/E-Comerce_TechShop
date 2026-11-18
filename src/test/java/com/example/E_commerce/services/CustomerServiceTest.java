package com.example.E_commerce.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.E_commerce.dto.requests.CustomerRequest;
import com.example.E_commerce.dto.responses.CustomerResponse;
import com.example.E_commerce.exception.InvalidPasswordException;
import com.example.E_commerce.exception.ResourceNotFoundException;
import com.example.E_commerce.mappers.CustomerMapper;
import com.example.E_commerce.models.Customer;
import com.example.E_commerce.models.Role;
import com.example.E_commerce.repository.CustomerRepository;
import com.example.E_commerce.security.CurrentUserService;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerReposity;

    @Mock
    private CurrentUserService currentUserService;

    private final CustomerMapper customerMapper = new CustomerMapper();
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private CustomerService customerService;

    @BeforeEach
    void setUp(){
        customerService = new CustomerService(customerReposity, customerMapper, passwordEncoder, currentUserService);
    }

    @Test
    void createStoresBcryptHashInsteadOfRawPassword(){
        CustomerRequest request = request("mysecret123");
        when(customerReposity.existsByEmail(request.getEmail())).thenReturn(false);
        when(customerReposity.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer customer = invocation.getArgument(0);
            customer.setId(1L);
            return customer;
        });

        CustomerResponse response = customerService.create(request);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerReposity).save(captor.capture());
        String storedHash = captor.getValue().getPasswordHash();

        assertThat(storedHash).isNotEqualTo("mysecret123");
        assertThat(storedHash).startsWith("$2");
        assertThat(passwordEncoder.matches("mysecret123", storedHash)).isTrue();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(captor.getValue().getRole()).isEqualTo(Role.USER);
        assertThat(response.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void updateReplacesPasswordWithNewBcryptHash(){
        Customer existing = customer(1L, Role.USER);
        existing.setPasswordHash(passwordEncoder.encode("oldsecret123"));
        when(customerReposity.findById(1L)).thenReturn(Optional.of(existing));
        when(currentUserService.getCurrentCustomer()).thenReturn(existing);
        when(customerReposity.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        customerService.update(1L, request("newsecret123"));

        assertThat(existing.getPasswordHash()).startsWith("$2");
        assertThat(passwordEncoder.matches("newsecret123", existing.getPasswordHash())).isTrue();
    }

    @Test
    void updateRejectsAnotherCustomersProfile(){
        Customer existing = customer(1L, Role.USER);
        Customer other = customer(2L, Role.USER);
        when(customerReposity.findById(1L)).thenReturn(Optional.of(existing));
        when(currentUserService.getCurrentCustomer()).thenReturn(other);
        when(currentUserService.isAdmin(other)).thenReturn(false);

        assertThatThrownBy(() -> customerService.update(1L, request("newsecret123")))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Customer not found");

        verify(customerReposity, never()).save(any(Customer.class));
    }

    @Test
    void getByIdHidesAnotherCustomersProfile(){
        Customer existing = customer(1L, Role.USER);
        Customer other = customer(2L, Role.USER);
        when(customerReposity.findById(1L)).thenReturn(Optional.of(existing));
        when(currentUserService.getCurrentCustomer()).thenReturn(other);
        when(currentUserService.isAdmin(other)).thenReturn(false);

        assertThatThrownBy(() -> customerService.getById(1L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Customer not found");
    }

    @Test
    void getByIdAllowsAdmin(){
        Customer existing = customer(1L, Role.USER);
        Customer admin = customer(2L, Role.ADMIN);
        when(customerReposity.findById(1L)).thenReturn(Optional.of(existing));
        when(currentUserService.getCurrentCustomer()).thenReturn(admin);
        when(currentUserService.isAdmin(admin)).thenReturn(true);

        CustomerResponse response = customerService.getById(1L);

        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    void createRejectsPasswordLongerThan72Bytes(){
        CustomerRequest request = request("a".repeat(73));

        assertThatThrownBy(() -> customerService.create(request))
            .isInstanceOf(InvalidPasswordException.class)
            .hasMessage("Password must be at most 72 bytes");
    }

    private Customer customer(Long id, Role role){
        Customer customer = new Customer();
        customer.setId(id);
        customer.setUserName("Reth Chhaiya");
        customer.setEmail(id == 1L ? "reth@example.com" : "other@example.com");
        customer.setPhone("012345678");
        customer.setRole(role);
        return customer;
    }

    private CustomerRequest request(String password){
        CustomerRequest request = new CustomerRequest();
        request.setUserName("Reth Chhaiya");
        request.setEmail("reth@example.com");
        request.setPhone("012345678");
        request.setPassword(password);
        return request;
    }
}
