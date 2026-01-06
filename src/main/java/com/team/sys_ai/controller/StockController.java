package com.team.sys_ai.controller;

import com.team.sys_ai.dto.PageResponse;
import com.team.sys_ai.dto.StockDTO;
import com.team.sys_ai.security.CustomUserDetailsService.CustomUserDetails;
import com.team.sys_ai.service.StockService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

        private final StockService stockService;

        /**
         * Get all stocks for a warehouse (paginated).
         */
        @GetMapping("/entrepot/{entrepotId}")
        public ResponseEntity<PageResponse<StockDTO>> getStocksByEntrepot(
                        @PathVariable Long entrepotId,
                        @PageableDefault(size = 20) Pageable pageable,
                        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
                return ResponseEntity.ok(PageResponse.from(
                                stockService.getStocksByEntrepot(entrepotId, userDetails.getUser(), pageable)));
        }

        /**
         * Get stocks at alert level for a warehouse (paginated).
         */
        @GetMapping("/entrepot/{entrepotId}/alerts")
        public ResponseEntity<PageResponse<StockDTO>> getStocksAtAlert(
                        @PathVariable Long entrepotId,
                        @PageableDefault(size = 20) Pageable pageable,
                        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
                return ResponseEntity.ok(PageResponse.from(
                                stockService.getStocksAtAlert(entrepotId, userDetails.getUser(), pageable)));
        }

        /**
         * Get critical stocks for a warehouse (paginated).
         */
        @GetMapping("/entrepot/{entrepotId}/critical")
        public ResponseEntity<PageResponse<StockDTO>> getCriticalStocks(
                        @PathVariable Long entrepotId,
                        @PageableDefault(size = 20) Pageable pageable,
                        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
                return ResponseEntity.ok(PageResponse.from(
                                stockService.getCriticalStocks(entrepotId, userDetails.getUser(), pageable)));
        }

        /**
         * Get all stocks at alert level (ADMIN only, paginated).
         */
        @GetMapping("/alerts/all")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<PageResponse<StockDTO>> getAllStocksAtAlert(
                        @PageableDefault(size = 20) Pageable pageable) {
                return ResponseEntity.ok(PageResponse.from(stockService.getAllStocksAtAlert(pageable)));
        }

        /**
         * Get stock for a specific product in a warehouse.
         */
        @GetMapping("/entrepot/{entrepotId}/produit/{produitId}")
        public ResponseEntity<StockDTO> getStock(
                        @PathVariable Long entrepotId,
                        @PathVariable Long produitId,
                        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
                return ResponseEntity.ok(stockService.getStock(entrepotId, produitId, userDetails.getUser()));
        }

        /**
         * Create or update stock.
         */
        @PutMapping("/entrepot/{entrepotId}/produit/{produitId}")
        public ResponseEntity<StockDTO> upsertStock(
                        @PathVariable Long entrepotId,
                        @PathVariable Long produitId,
                        @RequestParam Integer quantite,
                        @RequestParam(required = false) Integer seuilAlerte,
                        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
                return ResponseEntity.ok(
                                stockService.upsertStock(entrepotId, produitId, quantite, seuilAlerte,
                                                userDetails.getUser()));
        }

        /**
         * Add quantity to stock.
         */
        @PatchMapping("/entrepot/{entrepotId}/produit/{produitId}/add")
        public ResponseEntity<StockDTO> addQuantity(
                        @PathVariable Long entrepotId,
                        @PathVariable Long produitId,
                        @RequestParam Integer quantite,
                        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
                return ResponseEntity.ok(
                                stockService.addQuantity(entrepotId, produitId, quantite, userDetails.getUser()));
        }

        /**
         * Remove quantity from stock.
         */
        @PatchMapping("/entrepot/{entrepotId}/produit/{produitId}/remove")
        public ResponseEntity<StockDTO> removeQuantity(
                        @PathVariable Long entrepotId,
                        @PathVariable Long produitId,
                        @RequestParam Integer quantite,
                        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
                return ResponseEntity.ok(
                                stockService.removeQuantity(entrepotId, produitId, quantite, userDetails.getUser()));
        }

        /**
         * Update alert threshold.
         */
        @PatchMapping("/entrepot/{entrepotId}/produit/{produitId}/seuil")
        public ResponseEntity<StockDTO> updateSeuilAlerte(
                        @PathVariable Long entrepotId,
                        @PathVariable Long produitId,
                        @RequestParam Integer seuilAlerte,
                        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
                return ResponseEntity.ok(
                                stockService.updateSeuilAlerte(entrepotId, produitId, seuilAlerte,
                                                userDetails.getUser()));
        }

        /**
         * Get total stock for a product across all warehouses (ADMIN only).
         */
        @GetMapping("/produit/{produitId}/total")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Integer> getTotalQuantity(@PathVariable Long produitId) {
                return ResponseEntity.ok(stockService.getTotalQuantity(produitId));
        }
}
