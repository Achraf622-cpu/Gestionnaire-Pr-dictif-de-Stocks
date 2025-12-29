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


}
