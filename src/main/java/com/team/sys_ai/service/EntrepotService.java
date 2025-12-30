package com.team.sys_ai.service;

import com.team.sys_ai.dto.EntrepotDTO;
import com.team.sys_ai.entity.Entrepot;
import com.team.sys_ai.entity.Role;
import com.team.sys_ai.entity.User;
import com.team.sys_ai.exception.BusinessValidationException;
import com.team.sys_ai.exception.EntrepotAccessDeniedException;
import com.team.sys_ai.exception.ResourceNotFoundException;
import com.team.sys_ai.mapper.EntrepotMapper;
import com.team.sys_ai.repository.EntrepotRepository;
import com.team.sys_ai.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for Entrepot operations.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EntrepotService {

    private final EntrepotRepository entrepotRepository;
    private final StockRepository stockRepository;
    private final EntrepotMapper entrepotMapper;

    /**
     * Get all warehouses (ADMIN only).
     */
    public List<EntrepotDTO> getAllEntrepots() {
        return entrepotMapper.toDTOList(entrepotRepository.findAll());
    }

    /**
     * Get active warehouses.
     */
    public List<EntrepotDTO> getActiveEntrepots() {
        return entrepotMapper.toDTOList(entrepotRepository.findByActifTrue());
    }

    /**
     * Get warehouses accessible by user (role-based).
     */
    public List<EntrepotDTO> getAccessibleEntrepots(User user) {
        if (user.isAdmin()) {
            return getActiveEntrepots();
        } else {
            // GESTIONNAIRE can only see their assigned warehouse
            if (user.getEntrepotAssigne() == null) {
                return List.of();
            }
            return List.of(entrepotMapper.toDTO(user.getEntrepotAssigne()));
        }
    }

    /**
     * Find entrepot by ID or throw exception.
     */
    private Entrepot findById(Long id) {
        return entrepotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrepôt", "id", id));
    }

    /**
     * Get entrepot by ID.
     */
    public EntrepotDTO getEntrepotById(Long id) {
        Entrepot entrepot = findById(id);
        return entrepotMapper.toDTO(entrepot);
    }

    /**
     * Get entrepot by ID with access check.
     */
    public EntrepotDTO getEntrepotById(Long id, User user) {
        if (!user.hasAccessToEntrepot(id)) {
            throw new EntrepotAccessDeniedException(id, user.getId());
        }
        return getEntrepotById(id);
    }

    /**
     * Create new entrepot (ADMIN only).
     */
    @Transactional
    public EntrepotDTO createEntrepot(EntrepotDTO dto) {
        // Check for duplicate name
        if (entrepotRepository.existsByNomIgnoreCase(dto.getNom())) {
            throw new BusinessValidationException("nom", "Un entrepôt avec ce nom existe déjà");
        }

        Entrepot entrepot = entrepotMapper.toEntity(dto);
        entrepot.setActif(true);
        entrepot = entrepotRepository.save(entrepot);
        return entrepotMapper.toDTO(entrepot);
    }


    /**
     * Update entrepot (ADMIN only).
     */
    @Transactional
    public EntrepotDTO updateEntrepot(Long id, EntrepotDTO dto) {
        Entrepot entrepot = findById(id);

        // Check for duplicate name (excluding current)
        entrepotRepository.findByNomIgnoreCase(dto.getNom())
                .filter(e -> !e.getId().equals(id))
                .ifPresent(e -> {
                    throw new BusinessValidationException("nom", "Un entrepôt avec ce nom existe déjà");
                });

        entrepotMapper.updateEntity(dto, entrepot);
        entrepot = entrepotRepository.save(entrepot);
        return entrepotMapper.toDTO(entrepot);
    }

    /**
     * Delete entrepot (ADMIN only).
     */
    @Transactional
    public void deleteEntrepot(Long id) {
        Entrepot entrepot = findById(id);

        // Check if warehouse has stocks
        if (!entrepot.getStocks().isEmpty()) {
            throw new BusinessValidationException("Impossible de supprimer un entrepôt avec des stocks");
        }

        // Check if warehouse has assigned users
        if (!entrepot.getGestionnaires().isEmpty()) {
            throw new BusinessValidationException("Impossible de supprimer un entrepôt avec des gestionnaires assignés");
        }

        entrepotRepository.delete(entrepot);
    }

    /**
     * Deactivate entrepot (soft delete).
     */
    @Transactional
    public EntrepotDTO deactivateEntrepot(Long id) {
        Entrepot entrepot = findById(id);
        entrepot.setActif(false);
        entrepot = entrepotRepository.save(entrepot);
        return entrepotMapper.toDTO(entrepot);
    }


}
