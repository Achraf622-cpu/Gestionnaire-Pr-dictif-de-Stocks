package com.team.sys_ai.security;

import com.team.sys_ai.entity.Role;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        // Use a secret key that's at least 256 bits for HS256
        String secret = "myTestSecretKeyForJWTWhichMustBeAtLeast256BitsLongForHS256Algorithm";
        long expiration = 86400000; // 24 hours
        jwtUtil = new JwtUtil(secret, expiration);

        userDetails = new User(
            "testuser",
            "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
    }

    @Test
    @DisplayName("Should generate valid JWT token")
    void generateToken_ValidUser_ReturnsToken() {
        // When
        String token = jwtUtil.generateToken(userDetails);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts
    }

    @Test
    @DisplayName("Should extract username from token")
    void extractUsername_ValidToken_ReturnsUsername() {
        // Given
        String token = jwtUtil.generateToken(userDetails);

        // When
        String username = jwtUtil.extractUsername(token);

        // Then
        assertThat(username).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should extract role from token")
    void extractRole_ValidToken_ReturnsRole() {
        // Given
        String token = jwtUtil.generateToken("testuser", "ADMIN", null);

        // When
        String role = jwtUtil.extractRole(token);

        // Then
        assertThat(role).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("Should validate token against user details")
    void validateToken_ValidTokenAndUser_ReturnsTrue() {
        // Given
        String token = jwtUtil.generateToken(userDetails);

        // When
        Boolean isValid = jwtUtil.validateToken(token, userDetails);

        // Then
        assertThat(isValid).isTrue();
    }


}
