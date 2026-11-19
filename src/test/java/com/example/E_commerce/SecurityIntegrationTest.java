package com.example.E_commerce;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.example.E_commerce.models.Customer;
import com.example.E_commerce.models.Role;
import com.example.E_commerce.repository.CustomerRepository;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerReposity;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Customer user;
    private Customer other;

    @BeforeEach
    void setUp(){
        user = createCustomer("user@test.local", Role.USER);
        other = createCustomer("other@test.local", Role.USER);
        createCustomer("admin@test.local", Role.ADMIN);
    }

    @Test
    void publicCatalogReadsAreAnonymous() throws Exception {
        mockMvc.perform(get("/api/product")).andExpect(status().isOk());
        mockMvc.perform(get("/api/category")).andExpect(status().isOk());
    }

    @Test
    void protectedEndpointsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/customer")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/address")).andExpect(status().isUnauthorized());
    }

    @Test
    void registerReturnsCustomerAndUsableToken() throws Exception {
        String body = "{" +
            "\"userName\":\"New User\"," +
            "\"email\":\"new@test.local\"," +
            "\"phone\":\"012345678\"," +
            "\"password\":\"password123\"" +
        "}";

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.customerResponse.email").value("new@test.local"))
            .andExpect(jsonPath("$.customerResponse.role").value("USER"))
            .andExpect(jsonPath("$.tokenResponse.token").isNotEmpty())
            .andExpect(jsonPath("$.tokenResponse.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.tokenResponse.expiresIn").isNumber())
            .andReturn();

        String token = JsonPath.read(result.getResponse().getContentAsString(), "$.tokenResponse.token");

        mockMvc.perform(get("/api/customer/me")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("new@test.local"))
            .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @WithMockUser(username = "user@test.local", roles = "USER")
    void userCannotListAllCustomers() throws Exception {
        mockMvc.perform(get("/api/customer")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@test.local", roles = "USER")
    void userCanReadOwnProfileButNotOthers() throws Exception {
        mockMvc.perform(get("/api/customer/me"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("user@test.local"))
            .andExpect(jsonPath("$.role").value("USER"));

        mockMvc.perform(get("/api/customer/" + other.getId())).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@test.local", roles = "USER")
    void userCannotWriteCatalog() throws Exception {
        mockMvc.perform(post("/api/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test\",\"slug\":\"test\"}"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@test.local", roles = "USER")
    void userCanCreateOwnAddressButNotForAnotherCustomer() throws Exception {
        mockMvc.perform(post("/api/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addressJson(user.getId())))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.customerId").value(user.getId()));

        mockMvc.perform(post("/api/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addressJson(other.getId())))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@test.local", roles = "ADMIN")
    void adminCanListCustomers() throws Exception {
        mockMvc.perform(get("/api/customer")).andExpect(status().isOk());
    }

    private Customer createCustomer(String email, Role role){
        Customer customer = new Customer();
        customer.setUserName("Test " + role);
        customer.setEmail(email);
        customer.setPhone("012345678");
        customer.setPasswordHash(passwordEncoder.encode("password123"));
        customer.setCreatedAt(LocalDateTime.now());
        customer.setRole(role);
        return customerReposity.save(customer);
    }

    private String addressJson(Long customerId){
        return "{" +
            "\"customerId\":" + customerId + "," +
            "\"recipientName\":\"Recipient\"," +
            "\"phone\":\"012345678\"," +
            "\"addressLine\":\"Street 1\"," +
            "\"city\":\"Phnom Penh\"," +
            "\"province\":\"Phnom Penh\"," +
            "\"isDefault\":false" +
        "}";
    }
}
