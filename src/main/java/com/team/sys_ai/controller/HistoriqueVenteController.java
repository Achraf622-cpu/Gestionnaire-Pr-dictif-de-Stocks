package com.team.sys_ai.controller;

import com.team.sys_ai.dto.HistoriqueVenteDTO;
import com.team.sys_ai.security.CustomUserDetailsService.CustomUserDetails;
import com.team.sys_ai.service.HistoriqueVenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/historique")
@RequiredArgsConstructor
public class HistoriqueVenteController {

    private final HistoriqueVenteService historiqueVenteService;

    /**
     * Get sales history for a warehouse (paginated).
     */
    @GetMapping("/entrepot/{entrepotId}")
    public ResponseEntity<Page<HistoriqueVenteDTO>> getHistoriqueByEntrepot(
            @PathVariable Long entrepotId,
            @PageableDefault(size = 20, sort = "dateVente", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity
                .ok(historiqueVenteService.getHistoriqueByEntrepot(entrepotId, userDetails.getUser(), pageable));
    }

    /**
     * Get sales history by date range (paginated).
     */
    @GetMapping("/entrepot/{entrepotId}/range")
    public ResponseEntity<Page<HistoriqueVenteDTO>> getHistoriqueByDateRange(
            @PathVariable Long entrepotId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 20, sort = "dateVente", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                historiqueVenteService.getHistoriqueByEntrepotAndDateRange(entrepotId, startDate, endDate,
                        userDetails.getUser(), pageable));
    }

    /**
     * Get sales history for a product (paginated).
     */
    @GetMapping("/entrepot/{entrepotId}/produit/{produitId}")
    public ResponseEntity<Page<HistoriqueVenteDTO>> getHistoriqueByProduit(
            @PathVariable Long entrepotId,
            @PathVariable Long produitId,
            @PageableDefault(size = 20, sort = "dateVente", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                historiqueVenteService.getHistoriqueByProduitAndEntrepot(produitId, entrepotId, userDetails.getUser(),
                        pageable));
    }

    /**
     * Record a sale.
     */
    @PostMapping("/entrepot/{entrepotId}/produit/{produitId}")
    public ResponseEntity<HistoriqueVenteDTO> recordSale(
            @PathVariable Long entrepotId,
            @PathVariable Long produitId,
            @RequestParam Integer quantite,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateVente,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        HistoriqueVenteDTO result;
        if (dateVente != null) {
            result = historiqueVenteService.recordSale(entrepotId, produitId, quantite, dateVente,
                    userDetails.getUser());
        } else {
            result = historiqueVenteService.recordSale(entrepotId, produitId, quantite, userDetails.getUser());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Get total quantity sold analytics.
     */
    @GetMapping("/analytics/total/{entrepotId}/{produitId}")
    public ResponseEntity<Integer> getTotalQuantitySold(
            @PathVariable Long entrepotId,
            @PathVariable Long produitId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(
                historiqueVenteService.getTotalQuantitySold(produitId, entrepotId, startDate, endDate));
    }

    /**
     * Get average daily sales analytics.
     */
    @GetMapping("/analytics/average/{entrepotId}/{produitId}")
    public ResponseEntity<Double> getAverageDailySales(
            @PathVariable Long entrepotId,
            @PathVariable Long produitId,
            @RequestParam(defaultValue = "30") int daysBack) {
        return ResponseEntity.ok(
                historiqueVenteService.getAverageDailySales(produitId, entrepotId, daysBack));
    }

    /**
     * Get top selling products analytics.
     */
    @GetMapping("/analytics/top/{entrepotId}")
    public ResponseEntity<List<Object[]>> getTopSellingProducts(
            @PathVariable Long entrepotId,
            @RequestParam(defaultValue = "30") int daysBack,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(
                historiqueVenteService.getTopSellingProducts(entrepotId, daysBack, limit));
    }
}
