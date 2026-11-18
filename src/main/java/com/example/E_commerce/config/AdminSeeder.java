package com.example.E_commerce.config;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.E_commerce.models.Customer;
import com.example.E_commerce.models.Role;
import com.example.E_commerce.repository.CustomerRepository;

import jakarta.transaction.Transactional;

@Component
public class AdminSeeder implements ApplicationRunner {

    private final CustomerRepository customerReposity;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Value("${app.admin.username:Administrator}")
    private String adminUserName;

    public AdminSeeder(CustomerRepository customerReposity, PasswordEncoder passwordEncoder){
        this.customerReposity = customerReposity;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args){
        if(adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()){
            return;
        }

        Optional<Customer> existing = customerReposity.findByEmail(adminEmail);
        if(existing.isPresent()){
            Customer admin = existing.get();
            if(admin.getRole() != Role.ADMIN){
                admin.setRole(Role.ADMIN);
                customerReposity.save(admin);
            }
            return;
        }

        Customer admin = new Customer();
        admin.setUserName(adminUserName);
        admin.setEmail(adminEmail);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setCreatedAt(LocalDateTime.now());
        admin.setRole(Role.ADMIN);

        customerReposity.save(admin);
    }
}
