package com.team.sys_ai.service;

import com.team.sys_ai.dto.HistoriqueVenteDTO;
import com.team.sys_ai.entity.Entrepot;
import com.team.sys_ai.entity.HistoriqueVente;
import com.team.sys_ai.entity.Produit;
import com.team.sys_ai.entity.User;
import com.team.sys_ai.exception.BusinessValidationException;
import com.team.sys_ai.exception.EntrepotAccessDeniedException;
import com.team.sys_ai.exception.ResourceNotFoundException;
import com.team.sys_ai.mapper.HistoriqueVenteMapper;
import com.team.sys_ai.repository.EntrepotRepository;
import com.team.sys_ai.repository.HistoriqueVenteRepository;
import com.team.sys_ai.repository.ProduitRepository;
import com.team.sys_ai.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service for HistoriqueVente operations.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistoriqueVenteService {

    private final HistoriqueVenteRepository historiqueVenteRepository;
    private final ProduitRepository produitRepository;
    private final EntrepotRepository entrepotRepository;
    private final StockRepository stockRepository;
    private final StockService stockService;
    private final HistoriqueVenteMapper historiqueVenteMapper;


    /**
     * Validate user has access to warehouse.
     */
    private void validateAccess(Long entrepotId, User user) {
        if (!user.hasAccessToEntrepot(entrepotId)) {
            throw new EntrepotAccessDeniedException(entrepotId, user.getId());
        }
    }

    /**
     * Get sales history for a warehouse.
     */
    public List<HistoriqueVenteDTO> getHistoriqueByEntrepot(Long entrepotId, User user) {
        validateAccess(entrepotId, user);
        return historiqueVenteMapper.toDTOList(historiqueVenteRepository.findByEntrepotId(entrepotId));
    }

    /**
     * Get sales history between dates for a warehouse.
     */
    public List<HistoriqueVenteDTO> getHistoriqueByEntrepotAndDateRange(
            Long entrepotId, LocalDate startDate, LocalDate endDate, User user) {
        validateAccess(entrepotId, user);
        return historiqueVenteMapper.toDTOList(
                historiqueVenteRepository.findByEntrepotIdAndDateVenteBetween(entrepotId, startDate, endDate));
    }

    /**
     * Get sales history for a product in a warehouse.
     */
    public List<HistoriqueVenteDTO> getHistoriqueByProduitAndEntrepot(
            Long produitId, Long entrepotId, User user) {
        validateAccess(entrepotId, user);
        return historiqueVenteMapper.toDTOList(
                historiqueVenteRepository.findByProduitIdAndEntrepotId(produitId, entrepotId));
    }

    /**
     * Record a sale (and update stock).
     */
    @Transactional
    public HistoriqueVenteDTO recordSale(Long entrepotId, Long produitId, Integer quantite, User user) {
        return recordSale(entrepotId, produitId, quantite, LocalDate.now(), user);
    }
    /**
     * Record a sale with specific date.
     */
    @Transactional
    public HistoriqueVenteDTO recordSale(Long entrepotId, Long produitId, Integer quantite,
                                         LocalDate dateVente, User user) {
        validateAccess(entrepotId, user);

        if (quantite <= 0) {
            throw new BusinessValidationException("quantite", "La quantité vendue doit être positive");
        }

        Entrepot entrepot = entrepotRepository.findById(entrepotId)
                .orElseThrow(() -> new ResourceNotFoundException("Entrepôt", "id", entrepotId));

        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", "id", produitId));

        // Update stock (will throw if insufficient)
        stockService.removeQuantity(entrepotId, produitId, quantite, user);

        // Create sale record
        HistoriqueVente vente = HistoriqueVente.builder()
                .entrepot(entrepot)
                .produit(produit)
                .quantiteVendue(quantite)
                .dateVente(dateVente)
                .build();

        vente = historiqueVenteRepository.save(vente);
        return historiqueVenteMapper.toDTO(vente);
    }

    /**
     * Get total quantity sold for a product in a warehouse during a period.
     */
    public Integer getTotalQuantitySold(Long produitId, Long entrepotId, LocalDate startDate, LocalDate endDate) {
        return historiqueVenteRepository.getTotalQuantitySold(produitId, entrepotId, startDate, endDate);
    }

    /**
     * Get average daily sales.
     */
    public Double getAverageDailySales(Long produitId, Long entrepotId, int daysBack) {
        LocalDate startDate = LocalDate.now().minusDays(daysBack);
        return historiqueVenteRepository.getAverageDailySales(produitId, entrepotId, startDate);
    }

    /**
     * Get sales by day of week.
     */
    public List<Object[]> getSalesByDayOfWeek(Long produitId, Long entrepotId) {
        return historiqueVenteRepository.getSalesByDayOfWeek(produitId, entrepotId);
    }

    /**
     * Get monthly sales.
     */
    public List<Object[]> getMonthlySales(Long produitId, Long entrepotId) {
        return historiqueVenteRepository.getMonthlySales(produitId, entrepotId);
    }



}
