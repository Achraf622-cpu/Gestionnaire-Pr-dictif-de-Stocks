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


}
