package com.team.sys_ai.controller;

import com.team.sys_ai.dto.PageResponse;
import com.team.sys_ai.dto.PrevisionDTO;
import com.team.sys_ai.security.CustomUserDetailsService.CustomUserDetails;
import com.team.sys_ai.service.PrevisionService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
         * Get predictions for a warehouse (paginated).
         */
        @GetMapping("/entrepot/{entrepotId}")
        public ResponseEntity<PageResponse<PrevisionDTO>> getPrevisionsByEntrepot(
                        @PathVariable Long entrepotId,
                        @PageableDefault(size = 20) Pageable pageable,
                        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
                return ResponseEntity.ok(PageResponse.from(
                                previsionService.getPrevisionsByEntrepot(entrepotId, userDetails.getUser(), pageable)));
        }

        /**
         * Get high-risk predictions for a warehouse (paginated).
         */
        @GetMapping("/entrepot/{entrepotId}/high-risk")
        public ResponseEntity<PageResponse<PrevisionDTO>> getHighRiskPrevisions(
                        @PathVariable Long entrepotId,
                        @PageableDefault(size = 20) Pageable pageable,
                        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
                return ResponseEntity.ok(PageResponse.from(
                                previsionService.getHighRiskPrevisions(entrepotId, userDetails.getUser(), pageable)));
        }

        /**
         * Get all high-risk predictions (ADMIN only, paginated).
         */
        @GetMapping("/high-risk/all")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<PageResponse<PrevisionDTO>> getAllHighRiskPrevisions(
                        @PageableDefault(size = 20) Pageable pageable) {
                return ResponseEntity.ok(PageResponse.from(previsionService.getAllHighRiskPrevisions(pageable)));
        }

        /**
         * Get latest prediction for a product in a warehouse.
         */
        @GetMapping("/entrepot/{entrepotId}/produit/{produitId}/latest")
        public ResponseEntity<PrevisionDTO> getLatestPrevision(
                        @PathVariable Long entrepotId,
                        @PathVariable Long produitId,
                        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
                Optional<PrevisionDTO> prevision = previsionService.getLatestPrevision(entrepotId, produitId,
                                userDetails.getUser());
                return prevision.map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
        }

        /**
         * Generate prediction for a product in a warehouse.
         */
        @PostMapping("/entrepot/{entrepotId}/produit/{produitId}/generate")
        public ResponseEntity<PrevisionDTO> generatePrevision(
                        @PathVariable Long entrepotId,
                        @PathVariable Long produitId,
                        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(previsionService.generatePrevision(entrepotId, produitId, userDetails.getUser()));
        }

        /**
         * Generate predictions for all products in a warehouse.
         */
        @PostMapping("/entrepot/{entrepotId}/generate-all")
        public ResponseEntity<List<PrevisionDTO>> generatePrevisionsForEntrepot(
                        @PathVariable Long entrepotId,
                        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(previsionService.generatePrevisionsForEntrepot(entrepotId,
                                                userDetails.getUser()));
        }
}
