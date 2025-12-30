package com.team.sys_ai.controller;

import com.team.sys_ai.dto.ProduitAdminDTO;
import com.team.sys_ai.dto.ProduitDTO;
import com.team.sys_ai.security.CustomUserDetailsService.CustomUserDetails;
import com.team.sys_ai.service.ProduitService;
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
public class ProduitController {

    private final ProduitService produitService;

    /**
     * Get all products (without sensitive data).
     */
    @GetMapping("/produits")
    public ResponseEntity<List<ProduitDTO>> getAllProduits() {
        return ResponseEntity.ok(produitService.getAllProduits());
    }

    /**
     * Get all categories.
     */
    @GetMapping("/produits/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        return ResponseEntity.ok(produitService.getAllCategories());
    }

    /**
     * Get products by category.
     */
    @GetMapping("/produits/categorie/{categorie}")
    public ResponseEntity<List<ProduitDTO>> getProduitsByCategorie(@PathVariable String categorie) {
        return ResponseEntity.ok(produitService.getProduitsByCategorie(categorie));
    }

    /**
     * Search products by name.
     */
    @GetMapping("/produits/search")
    public ResponseEntity<List<ProduitDTO>> searchProduits(@RequestParam String nom) {
        return ResponseEntity.ok(produitService.searchProduits(nom));
    }

    /**
     * Get product by ID (role-based response).
     */
    @GetMapping("/produits/{id}")
    public ResponseEntity<?> getProduitById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(produitService.getProduitById(id, userDetails.getUser()));
    }

    // ===== ADMIN ONLY ENDPOINTS =====

    /**
     * Get all products with sensitive data (ADMIN only).
     */
    @GetMapping("/admin/produits")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProduitAdminDTO>> getAllProduitsForAdmin() {
        return ResponseEntity.ok(produitService.getAllProduitsForAdmin());
    }


}
