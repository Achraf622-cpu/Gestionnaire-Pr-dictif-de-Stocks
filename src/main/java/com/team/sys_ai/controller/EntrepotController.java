package com.team.sys_ai.controller;

import com.team.sys_ai.dto.EntrepotDTO;
import com.team.sys_ai.entity.User;
import com.team.sys_ai.security.CustomUserDetailsService.CustomUserDetails;
import com.team.sys_ai.service.EntrepotService;
import com.team.sys_ai.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EntrepotController {

    private final EntrepotService entrepotService;


    @GetMapping("/entrepots")
    public ResponseEntity<List<EntrepotDTO>> getAccessibleEntrepots(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(entrepotService.getAccessibleEntrepots(userDetails.getUser()));
    }


    @GetMapping("/entrepots/{id}")
    public ResponseEntity<EntrepotDTO> getEntrepotById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(entrepotService.getEntrepotById(id, userDetails.getUser()));
    }

    @GetMapping("/entrepots/{id}/stats")
    public ResponseEntity<EntrepotDTO> getEntrepotWithStats(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (!userDetails.getUser().hasAccessToEntrepot(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(entrepotService.getEntrepotWithStats(id));
    }

    @GetMapping("/admin/entrepots")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EntrepotDTO>> getAllEntrepots() {
        return ResponseEntity.ok(entrepotService.getAllEntrepots());
    }


    @PostMapping("/admin/entrepots")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EntrepotDTO> createEntrepot(@Valid @RequestBody EntrepotDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(entrepotService.createEntrepot(dto));
    }


}
