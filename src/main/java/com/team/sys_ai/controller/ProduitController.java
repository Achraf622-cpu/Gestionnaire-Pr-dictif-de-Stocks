package com.team.sys_ai.controller;

import com.team.sys_ai.dto.PageResponse;
import com.team.sys_ai.dto.ProduitAdminDTO;
import com.team.sys_ai.dto.ProduitDTO;
import com.team.sys_ai.security.CustomUserDetailsService.CustomUserDetails;
import com.team.sys_ai.service.ProduitService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitService produitService;

    /**
     * Get all products (paginated, without sensitive data).
     */
    @GetMapping("/produits")
    public ResponseEntity<PageResponse<ProduitDTO>> getAllProduits(
            @PageableDefault(size = 20, sort = "nom") Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(produitService.getAllProduits(pageable)));
    }

    /**
     * Get all categories.
     */
    @GetMapping("/produits/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        return ResponseEntity.ok(produitService.getAllCategories());
    }

    /**
     * Get products by category (paginated).
     */
    @GetMapping("/produits/categorie/{categorie}")
    public ResponseEntity<PageResponse<ProduitDTO>> getProduitsByCategorie(
            @PathVariable String categorie,
            @PageableDefault(size = 20, sort = "nom") Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(produitService.getProduitsByCategorie(categorie, pageable)));
    }

    /**
     * Search products by name (paginated).
     */
    @GetMapping("/produits/search")
    public ResponseEntity<PageResponse<ProduitDTO>> searchProduits(
            @RequestParam String nom,
            @PageableDefault(size = 20, sort = "nom") Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(produitService.searchProduits(nom, pageable)));
    }

    /**
     * Get product by ID (role-based response).
     */
    @GetMapping("/produits/{id}")
    public ResponseEntity<?> getProduitById(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(produitService.getProduitById(id, userDetails.getUser()));
    }

    // ===== ADMIN ONLY ENDPOINTS =====

    /**
     * Get all products with sensitive data (ADMIN only, paginated).
     */
    @GetMapping("/admin/produits")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<ProduitAdminDTO>> getAllProduitsForAdmin(
            @PageableDefault(size = 20, sort = "nom") Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(produitService.getAllProduitsForAdmin(pageable)));
    }

    /**
     * Get product with sensitive data (ADMIN only).
     */
    @GetMapping("/admin/produits/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProduitAdminDTO> getProduitByIdForAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(produitService.getProduitByIdForAdmin(id));
    }

    /**
     * Create new product (ADMIN only).
     */
    @PostMapping("/admin/produits")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProduitAdminDTO> createProduit(@Valid @RequestBody ProduitAdminDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produitService.createProduit(dto));
    }

    /**
     * Update product (ADMIN only).
     */
    @PutMapping("/admin/produits/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProduitAdminDTO> updateProduit(
            @PathVariable Long id,
            @Valid @RequestBody ProduitAdminDTO dto) {
        return ResponseEntity.ok(produitService.updateProduit(id, dto));
    }

    /**
     * Deactivate product (ADMIN only).
     */
    @PatchMapping("/admin/produits/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProduitAdminDTO> deactivateProduit(@PathVariable Long id) {
        return ResponseEntity.ok(produitService.deactivateProduit(id));
    }

    /**
     * Delete product (ADMIN only).
     */
    @DeleteMapping("/admin/produits/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduit(@PathVariable Long id) {
        produitService.deleteProduit(id);
        return ResponseEntity.noContent().build();
    }
}
