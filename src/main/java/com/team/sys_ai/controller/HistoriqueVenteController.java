package com.team.sys_ai.controller;

import com.team.sys_ai.dto.HistoriqueVenteDTO;
import com.team.sys_ai.security.CustomUserDetailsService.CustomUserDetails;
import com.team.sys_ai.service.HistoriqueVenteService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/entrepot/{entrepotId}")
    public ResponseEntity<List<HistoriqueVenteDTO>> getHistoriqueByEntrepot(
            @PathVariable Long entrepotId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(historiqueVenteService.getHistoriqueByEntrepot(entrepotId, userDetails.getUser()));
    }


    @GetMapping("/entrepot/{entrepotId}/range")
    public ResponseEntity<List<HistoriqueVenteDTO>> getHistoriqueByDateRange(
            @PathVariable Long entrepotId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
            historiqueVenteService.getHistoriqueByEntrepotAndDateRange(entrepotId, startDate, endDate, userDetails.getUser()));
    }

    @GetMapping("/entrepot/{entrepotId}/produit/{produitId}")
    public ResponseEntity<List<HistoriqueVenteDTO>> getHistoriqueByProduit(
            @PathVariable Long entrepotId,
            @PathVariable Long produitId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
            historiqueVenteService.getHistoriqueByProduitAndEntrepot(produitId, entrepotId, userDetails.getUser()));
    }

    @PostMapping("/entrepot/{entrepotId}/produit/{produitId}")
    public ResponseEntity<HistoriqueVenteDTO> recordSale(
            @PathVariable Long entrepotId,
            @PathVariable Long produitId,
            @RequestParam Integer quantite,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateVente,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        HistoriqueVenteDTO result;
        if (dateVente != null) {
            result = historiqueVenteService.recordSale(entrepotId, produitId, quantite, dateVente, userDetails.getUser());
        } else {
            result = historiqueVenteService.recordSale(entrepotId, produitId, quantite, userDetails.getUser());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/analytics/total/{entrepotId}/{produitId}")
    public ResponseEntity<Integer> getTotalQuantitySold(
            @PathVariable Long entrepotId,
            @PathVariable Long produitId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(
            historiqueVenteService.getTotalQuantitySold(produitId, entrepotId, startDate, endDate));
    }


    @GetMapping("/analytics/average/{entrepotId}/{produitId}")
    public ResponseEntity<Double> getAverageDailySales(
            @PathVariable Long entrepotId,
            @PathVariable Long produitId,
            @RequestParam(defaultValue = "30") int daysBack) {
        return ResponseEntity.ok(
            historiqueVenteService.getAverageDailySales(produitId, entrepotId, daysBack));
    }


    @GetMapping("/analytics/top/{entrepotId}")
    public ResponseEntity<List<Object[]>> getTopSellingProducts(
            @PathVariable Long entrepotId,
            @RequestParam(defaultValue = "30") int daysBack,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(
            historiqueVenteService.getTopSellingProducts(entrepotId, daysBack, limit));
    }
}
