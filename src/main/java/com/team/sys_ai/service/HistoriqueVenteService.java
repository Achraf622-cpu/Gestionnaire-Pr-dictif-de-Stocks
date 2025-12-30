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



}
