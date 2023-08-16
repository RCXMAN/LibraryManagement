package com.example.librarymanagement.service;

import com.example.librarymanagement.service.Imp.JwtService;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JwtServiceTest {
    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    @Test
    void testGenerateTokenAndExtractUsername() {
        UserDetails userDetails = User.builder()
                .username("testUser")
                .password("testPassword")
                .roles("USER")
                .build();

        JwtService jwtService = new JwtService();

        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);

        String extractedUsername = jwtService.extractUsername(token);
        assertEquals("testUser", extractedUsername);
    }

    @Test
    void testIsTokenValid() {
        UserDetails userDetails = User.builder()
                .username("testUser")
                .password("testPassword")
                .roles("USER")
                .build();

        JwtService jwtService = new JwtService();

        String token = jwtService.generateToken(userDetails);

        Assertions.assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
