package com.team.sys_ai.service;

import com.team.sys_ai.dto.AuthRequestDTO;
import com.team.sys_ai.dto.AuthResponseDTO;
import com.team.sys_ai.entity.User;
import com.team.sys_ai.exception.InvalidCredentialsException;
import com.team.sys_ai.repository.UserRepository;
import com.team.sys_ai.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponseDTO authenticate(AuthRequestDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
            );

            User user = userRepository.findActiveByLogin(request.getLogin())
                    .orElseThrow(() -> new InvalidCredentialsException("Utilisateur non trouvé"));

            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            String token = jwtUtil.generateToken(
                    user.getLogin(),
                    user.getRole().name(),
                    user.getEntrepotAssigne() != null ? user.getEntrepotAssigne().getId() : null
            );

            return AuthResponseDTO.of(
                    token,
                    user.getId(),
                    user.getLogin(),
                    user.getNom(),
                    user.getPrenom(),
                    user.getRole(),
                    user.getEntrepotAssigne() != null ? user.getEntrepotAssigne().getId() : null,
                    user.getEntrepotAssigne() != null ? user.getEntrepotAssigne().getNom() : null
            );

        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Login ou mot de passe incorrect");
        }
    }

}
