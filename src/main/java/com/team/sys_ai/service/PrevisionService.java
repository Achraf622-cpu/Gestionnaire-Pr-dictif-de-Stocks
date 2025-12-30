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

    /**
     * Get latest prediction for a product in a warehouse.
     */
    public Optional<PrevisionDTO> getLatestPrevision(Long entrepotId, Long produitId, User user) {
        validateAccess(entrepotId, user);
        return previsionRepository.findLatestByProduitIdAndEntrepotId(produitId, entrepotId)
                .map(p -> enrichPrevision(p, entrepotId));
    }

    /**
     * Generate prediction for a product in a warehouse.
     */
    @Transactional
    public PrevisionDTO generatePrevision(Long entrepotId, Long produitId, User user) {
        validateAccess(entrepotId, user);

        Entrepot entrepot = entrepotRepository.findById(entrepotId)
                .orElseThrow(() -> new ResourceNotFoundException("Entrepôt", "id", entrepotId));

        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", "id", produitId));

        // Get historical data
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        LocalDate ninetyDaysAgo = LocalDate.now().minusDays(90);

        Integer totalSold30Days = historiqueVenteRepository.getTotalQuantitySold(
                produitId, entrepotId, thirtyDaysAgo, LocalDate.now());

        Double avgDailySales = historiqueVenteRepository.getAverageDailySales(
                produitId, entrepotId, ninetyDaysAgo);

        Long salesRecordCount = historiqueVenteRepository.countSalesRecords(
                produitId, entrepotId, ninetyDaysAgo);

        // Get current stock
        Integer currentStock = stockRepository.findByEntrepotIdAndProduitId(entrepotId, produitId)
                .map(Stock::getQuantiteDisponible)
                .orElse(0);

        Integer seuilAlerte = stockRepository.findByEntrepotIdAndProduitId(entrepotId, produitId)
                .map(Stock::getSeuilAlerte)
                .orElse(10);

        // Calculate prediction
        int predictedSales30Days = calculatePredictedSales(avgDailySales, totalSold30Days);
        double confidence = calculateConfidence(salesRecordCount);
        Prevision.NiveauRisque riskLevel = calculateRiskLevel(currentStock, predictedSales30Days, seuilAlerte);
        String recommendation = generateRecommendation(
                produit.getNom(), currentStock, predictedSales30Days, seuilAlerte, riskLevel);
        Integer quantiteRecommandee = calculateRecommendedQuantity(
                currentStock, predictedSales30Days, seuilAlerte);

        // Create prediction
        Prevision prevision = Prevision.builder()
                .produit(produit)
                .entrepot(entrepot)
                .datePrevision(LocalDate.now())
                .quantitePrevue30Jours(predictedSales30Days)
                .niveauConfiance(confidence)
                .niveauRisque(riskLevel)
                .recommandation(recommendation)
                .quantiteRecommandee(quantiteRecommandee)
                .build();

        prevision = previsionRepository.save(prevision);

        PrevisionDTO dto = previsionMapper.toDTO(prevision);
        dto.setStockActuel(currentStock);
        dto.setSeuilAlerte(seuilAlerte);
        return dto;
    }

    /**
     * Generate predictions for all products in a warehouse.
     */
    @Transactional
    public List<PrevisionDTO> generatePrevisionsForEntrepot(Long entrepotId, User user) {
        validateAccess(entrepotId, user);

        List<Stock> stocks = stockRepository.findByEntrepotId(entrepotId);
        return stocks.stream()
                .map(stock -> generatePrevision(entrepotId, stock.getProduit().getId(), user))
                .toList();
    }



}
