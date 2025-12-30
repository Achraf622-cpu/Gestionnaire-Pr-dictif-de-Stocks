package com.team.sys_ai.controller;

import com.team.sys_ai.dto.PrevisionDTO;
import com.team.sys_ai.security.CustomUserDetailsService.CustomUserDetails;
import com.team.sys_ai.service.PrevisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/previsions")
@RequiredArgsConstructor
public class PrevisionController {

    private final PrevisionService previsionService;

    /**
     * Get predictions for a warehouse.
     */
    @GetMapping("/entrepot/{entrepotId}")
    public ResponseEntity<List<PrevisionDTO>> getPrevisionsByEntrepot(
            @PathVariable Long entrepotId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(previsionService.getPrevisionsByEntrepot(entrepotId, userDetails.getUser()));
    }

    /**
     * Get high-risk predictions for a warehouse.
     */
    @GetMapping("/entrepot/{entrepotId}/high-risk")
    public ResponseEntity<List<PrevisionDTO>> getHighRiskPrevisions(
            @PathVariable Long entrepotId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(previsionService.getHighRiskPrevisions(entrepotId, userDetails.getUser()));
    }

    /**
     * Get all high-risk predictions (ADMIN only).
     */
    @GetMapping("/high-risk/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PrevisionDTO>> getAllHighRiskPrevisions() {
        return ResponseEntity.ok(previsionService.getAllHighRiskPrevisions());
    }


}
