package com.team.sys_ai.service;

import com.team.sys_ai.dto.PrevisionDTO;
import com.team.sys_ai.entity.*;
import com.team.sys_ai.exception.EntrepotAccessDeniedException;
import com.team.sys_ai.exception.ResourceNotFoundException;
import com.team.sys_ai.mapper.PrevisionMapper;
import com.team.sys_ai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service for AI-powered stock predictions.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class    PrevisionService {

    private final PrevisionRepository previsionRepository;
    private final StockRepository stockRepository;
    private final HistoriqueVenteRepository historiqueVenteRepository;
    private final ProduitRepository produitRepository;
    private final EntrepotRepository entrepotRepository;
    private final PrevisionMapper previsionMapper;
    private final Optional<ChatClient.Builder> chatClientBuilder;


    /**
     * Validate user has access to warehouse.
     */
    private void validateAccess(Long entrepotId, User user) {
        if (!user.hasAccessToEntrepot(entrepotId)) {
            throw new EntrepotAccessDeniedException(entrepotId, user.getId());
        }
    }

    /**
     * Enrich list of predictions.
     */
    private List<PrevisionDTO> enrichPrevisions(List<Prevision> previsions, Long entrepotId) {
        return previsions.stream()
                .map(p -> enrichPrevision(p, entrepotId))
                .toList();
    }


    /**
     * Get predictions for a warehouse.
     */
    public List<PrevisionDTO> getPrevisionsByEntrepot(Long entrepotId, User user) {
        validateAccess(entrepotId, user);
        List<Prevision> previsions = previsionRepository.findByEntrepotId(entrepotId);
        return enrichPrevisions(previsions, entrepotId);
    }

    /**
     * Get high-risk predictions for a warehouse.
     */
    public List<PrevisionDTO> getHighRiskPrevisions(Long entrepotId, User user) {
        validateAccess(entrepotId, user);
        List<Prevision> previsions = previsionRepository.findHighRiskPredictions(entrepotId);
        return enrichPrevisions(previsions, entrepotId);
    }

    /**
     * Get all high-risk predictions (ADMIN only).
     */
    public List<PrevisionDTO> getAllHighRiskPrevisions() {
        List<Prevision> previsions = previsionRepository.findAllHighRiskPredictions();
        return previsionMapper.toDTOList(previsions);
    }


}
