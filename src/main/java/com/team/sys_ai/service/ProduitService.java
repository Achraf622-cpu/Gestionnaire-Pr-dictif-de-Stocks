package com.team.sys_ai.service;

import com.team.sys_ai.dto.ProduitAdminDTO;
import com.team.sys_ai.dto.ProduitDTO;
import com.team.sys_ai.entity.Produit;
import com.team.sys_ai.entity.Role;
import com.team.sys_ai.entity.User;
import com.team.sys_ai.exception.BusinessValidationException;
import com.team.sys_ai.exception.ResourceNotFoundException;
import com.team.sys_ai.mapper.ProduitMapper;
import com.team.sys_ai.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProduitService {

    private final ProduitRepository produitRepository;
    private final ProduitMapper produitMapper;


    public List<ProduitDTO> getAllProduits() {
        return produitMapper.toDTOList(produitRepository.findByActifTrue());
    }


    public List<ProduitAdminDTO> getAllProduitsForAdmin() {
        return produitMapper.toAdminDTOList(produitRepository.findByActifTrue());
    }


    public List<ProduitDTO> getProduitsByCategorie(String categorie) {
        return produitMapper.toDTOList(produitRepository.findByCategorieAndActifTrue(categorie));
    }


    public List<ProduitDTO> searchProduits(String nom) {
        return produitMapper.toDTOList(produitRepository.findByNomContainingIgnoreCase(nom));
    }


    public List<String> getAllCategories() {
        return produitRepository.findAllCategories();
    }


    public Object getProduitById(Long id, User user) {
        Produit produit = findById(id);
        if (user.isAdmin()) {
            return produitMapper.toAdminDTO(produit);
        }
        return produitMapper.toDTO(produit);
    }


    public ProduitDTO getProduitById(Long id) {
        return produitMapper.toDTO(findById(id));
    }

    public ProduitAdminDTO getProduitByIdForAdmin(Long id) {
        return produitMapper.toAdminDTO(findById(id));
    }

    @Transactional
    public ProduitAdminDTO createProduit(ProduitAdminDTO dto) {
        // Check for duplicate name
        if (produitRepository.existsByNomIgnoreCase(dto.getNom())) {
            throw new BusinessValidationException("nom", "Un produit avec ce nom existe déjà");
        }

        Produit produit = produitMapper.toEntityFromAdmin(dto);
        produit.setActif(true);
        produit = produitRepository.save(produit);
        return produitMapper.toAdminDTO(produit);
    }

    @Transactional
    public ProduitAdminDTO updateProduit(Long id, ProduitAdminDTO dto) {
        Produit produit = findById(id);

        // Check for duplicate name (excluding current)
        produitRepository.findByNomIgnoreCase(dto.getNom())
            .filter(p -> !p.getId().equals(id))
            .ifPresent(p -> {
                throw new BusinessValidationException("nom", "Un produit avec ce nom existe déjà");
            });

        produitMapper.updateEntityFromAdmin(dto, produit);
        produit = produitRepository.save(produit);
        return produitMapper.toAdminDTO(produit);
    }

    @Transactional
    public void deleteProduit(Long id) {
        Produit produit = findById(id);

        if (!produit.getStocks().isEmpty()) {
            throw new BusinessValidationException("Impossible de supprimer un produit avec des stocks");
        }

        produitRepository.delete(produit);
    }

    @Transactional
    public ProduitAdminDTO deactivateProduit(Long id) {
        Produit produit = findById(id);
        produit.setActif(false);
        produit = produitRepository.save(produit);
        return produitMapper.toAdminDTO(produit);
    }

    private Produit findById(Long id) {
        return produitRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Produit", "id", id));
    }
}
