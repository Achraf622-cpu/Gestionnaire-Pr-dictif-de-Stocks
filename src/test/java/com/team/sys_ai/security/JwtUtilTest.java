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


}
