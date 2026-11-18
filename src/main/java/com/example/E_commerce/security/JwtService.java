package com.example.E_commerce.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.example.E_commerce.dto.responses.TokenResponse;
import com.example.E_commerce.models.Customer;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final long expirationMinutes;

    public JwtService(JwtEncoder jwtEncoder, @Value("${app.jwt.expiration-minutes:60}") long expirationMinutes){
        this.jwtEncoder = jwtEncoder;
        this.expirationMinutes = expirationMinutes;
    }

    public TokenResponse generateToken(Customer customer){
        Instant now = Instant.now();
        Instant expiresAt = now.plus(expirationMinutes, ChronoUnit.MINUTES);
        //create header
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        //create a ID card that contain user information
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .subject(customer.getEmail())
            .claim("customerId", customer.getId())
            .claim("role", customer.getRole().name())
            .issuedAt(now)
            .expiresAt(expiresAt)
            .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        
        return new TokenResponse(token, "Bearer", expiresAt.getEpochSecond() - now.getEpochSecond());
    }
}
