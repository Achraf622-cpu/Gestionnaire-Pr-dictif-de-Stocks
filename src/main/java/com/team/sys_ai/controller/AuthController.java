package com.team.sys_ai.controller;

import com.team.sys_ai.dto.AuthRequestDTO;
import com.team.sys_ai.dto.AuthResponseDTO;
import com.team.sys_ai.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        AuthResponseDTO response = authenticationService.authenticate(request);
        return ResponseEntity.ok(response);
    }
}
