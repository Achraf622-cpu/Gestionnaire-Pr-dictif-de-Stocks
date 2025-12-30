package com.team.sys_ai.controller;

import com.team.sys_ai.dto.StockDTO;
import com.team.sys_ai.security.CustomUserDetailsService.CustomUserDetails;
import com.team.sys_ai.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    /**
     * Get all stocks for a warehouse.
     */
    @GetMapping("/entrepot/{entrepotId}")
    public ResponseEntity<List<StockDTO>> getStocksByEntrepot(
            @PathVariable Long entrepotId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(stockService.getStocksByEntrepot(entrepotId, userDetails.getUser()));
    }

    /**
     * Get stocks at alert level for a warehouse.
     */
    @GetMapping("/entrepot/{entrepotId}/alerts")
    public ResponseEntity<List<StockDTO>> getStocksAtAlert(
            @PathVariable Long entrepotId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(stockService.getStocksAtAlert(entrepotId, userDetails.getUser()));
    }


}
