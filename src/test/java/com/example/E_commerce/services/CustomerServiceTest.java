package com.example.E_commerce.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
import com.example.E_commerce.mappers.CustomerMapper;
import com.example.E_commerce.models.Customer;
import com.example.E_commerce.repository.CustomerReposity;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerReposity customerReposity;

    private final CustomerMapper customerMapper = new CustomerMapper();
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private CustomerService customerService;

    @BeforeEach
    void setUp(){
        customerService = new CustomerService(customerReposity, customerMapper, passwordEncoder);
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
    }

    @Test
    void updateReplacesPasswordWithNewBcryptHash(){
        Customer existing = new Customer();
        existing.setId(1L);
        existing.setPasswordHash(passwordEncoder.encode("oldsecret123"));
        when(customerReposity.findById(1L)).thenReturn(Optional.of(existing));
        when(customerReposity.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        customerService.update(1L, request("newsecret123"));

        assertThat(existing.getPasswordHash()).startsWith("$2");
        assertThat(passwordEncoder.matches("newsecret123", existing.getPasswordHash())).isTrue();
    }

    @Test
    void createRejectsPasswordLongerThan72Bytes(){
        CustomerRequest request = request("a".repeat(73));

        assertThatThrownBy(() -> customerService.create(request))
            .isInstanceOf(InvalidPasswordException.class)
            .hasMessage("Password must be at most 72 bytes");
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
