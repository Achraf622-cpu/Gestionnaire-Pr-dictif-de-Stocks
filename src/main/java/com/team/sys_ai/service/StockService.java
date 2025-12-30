package com.team.sys_ai.service;

import com.team.sys_ai.dto.StockDTO;
import com.team.sys_ai.entity.Entrepot;
import com.team.sys_ai.entity.Produit;
import com.team.sys_ai.entity.Stock;
import com.team.sys_ai.entity.User;
import com.team.sys_ai.exception.BusinessValidationException;
import com.team.sys_ai.exception.EntrepotAccessDeniedException;
import com.team.sys_ai.exception.ResourceNotFoundException;
import com.team.sys_ai.mapper.StockMapper;
import com.team.sys_ai.repository.EntrepotRepository;
import com.team.sys_ai.repository.ProduitRepository;
import com.team.sys_ai.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {

    private final StockRepository stockRepository;
    private final ProduitRepository produitRepository;
    private final EntrepotRepository entrepotRepository;
    private final StockMapper stockMapper;

    public List<StockDTO> getStocksByEntrepot(Long entrepotId, User user) {
        validateAccess(entrepotId, user);
        return stockMapper.toDTOList(stockRepository.findByEntrepotIdWithDetails(entrepotId));
    }

    public List<StockDTO> getStocksAtAlert(Long entrepotId, User user) {
        validateAccess(entrepotId, user);
        return stockMapper.toDTOList(stockRepository.findStocksAtAlertLevelByEntrepot(entrepotId));
    }

    public List<StockDTO> getCriticalStocks(Long entrepotId, User user) {
        validateAccess(entrepotId, user);
        return stockMapper.toDTOList(stockRepository.findCriticalStocksByEntrepot(entrepotId));
    }

    public List<StockDTO> getAllStocksAtAlert() {
        return stockMapper.toDTOList(stockRepository.findStocksAtAlertLevel());
    }

    public StockDTO getStock(Long entrepotId, Long produitId, User user) {
        validateAccess(entrepotId, user);
        Stock stock = stockRepository.findByEntrepotIdAndProduitId(entrepotId, produitId)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("Stock non trouvé pour produit %d dans entrepôt %d", produitId, entrepotId)));
        return stockMapper.toDTO(stock);
    }

    @Transactional
    public StockDTO upsertStock(Long entrepotId, Long produitId, Integer quantite, Integer seuilAlerte, User user) {
        validateAccess(entrepotId, user);

        Stock stock = stockRepository.findByEntrepotIdAndProduitId(entrepotId, produitId)
            .orElseGet(() -> createNewStock(entrepotId, produitId));

        stock.setQuantiteDisponible(quantite);
        if (seuilAlerte != null) {
            stock.setSeuilAlerte(seuilAlerte);
        }

        stock = stockRepository.save(stock);
        return stockMapper.toDTO(stock);
    }

    @Transactional
    public StockDTO updateQuantity(Long entrepotId, Long produitId, Integer quantity, User user) {
        validateAccess(entrepotId, user);

        Stock stock = stockRepository.findByEntrepotIdAndProduitId(entrepotId, produitId)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("Stock non trouvé pour produit %d dans entrepôt %d", produitId, entrepotId)));

        stock.setQuantiteDisponible(quantity);
        stock = stockRepository.save(stock);
        return stockMapper.toDTO(stock);
    }

    @Transactional
    public StockDTO addQuantity(Long entrepotId, Long produitId, Integer quantityToAdd, User user) {
        validateAccess(entrepotId, user);

        if (quantityToAdd <= 0) {
            throw new BusinessValidationException("quantite", "La quantité à ajouter doit être positive");
        }

        Stock stock = stockRepository.findByEntrepotIdAndProduitId(entrepotId, produitId)
            .orElseGet(() -> createNewStock(entrepotId, produitId));

        stock.setQuantiteDisponible(stock.getQuantiteDisponible() + quantityToAdd);
        stock = stockRepository.save(stock);
        return stockMapper.toDTO(stock);
    }

    @Transactional
    public StockDTO removeQuantity(Long entrepotId, Long produitId, Integer quantityToRemove, User user) {
        validateAccess(entrepotId, user);

        if (quantityToRemove <= 0) {
            throw new BusinessValidationException("quantite", "La quantité à retirer doit être positive");
        }

        Stock stock = stockRepository.findByEntrepotIdAndProduitId(entrepotId, produitId)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("Stock non trouvé pour produit %d dans entrepôt %d", produitId, entrepotId)));

        int newQuantity = stock.getQuantiteDisponible() - quantityToRemove;
        if (newQuantity < 0) {
            throw new BusinessValidationException("quantite", 
                String.format("Stock insuffisant. Disponible: %d, Demandé: %d", 
                    stock.getQuantiteDisponible(), quantityToRemove));
        }

        stock.setQuantiteDisponible(newQuantity);
        stock = stockRepository.save(stock);
        return stockMapper.toDTO(stock);
    }

    @Transactional
    public StockDTO updateSeuilAlerte(Long entrepotId, Long produitId, Integer seuilAlerte, User user) {
        validateAccess(entrepotId, user);

        Stock stock = stockRepository.findByEntrepotIdAndProduitId(entrepotId, produitId)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("Stock non trouvé pour produit %d dans entrepôt %d", produitId, entrepotId)));

        stock.setSeuilAlerte(seuilAlerte);
        stock = stockRepository.save(stock);
        return stockMapper.toDTO(stock);
    }

    public Integer getTotalQuantity(Long produitId) {
        return stockRepository.getTotalQuantityByProduit(produitId);
    }

    private Stock createNewStock(Long entrepotId, Long produitId) {
        Entrepot entrepot = entrepotRepository.findById(entrepotId)
            .orElseThrow(() -> new ResourceNotFoundException("Entrepôt", "id", entrepotId));

        Produit produit = produitRepository.findById(produitId)
            .orElseThrow(() -> new ResourceNotFoundException("Produit", "id", produitId));

        return Stock.builder()
            .entrepot(entrepot)
            .produit(produit)
            .quantiteDisponible(0)
            .seuilAlerte(10) // Default threshold
            .build();
    }

    private void validateAccess(Long entrepotId, User user) {
        if (!user.hasAccessToEntrepot(entrepotId)) {
            throw new EntrepotAccessDeniedException(entrepotId, user.getId());
        }
    }
}
