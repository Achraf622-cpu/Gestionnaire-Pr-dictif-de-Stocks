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

    /**
     * Calculate predicted sales based on historical data.
     */
    private int calculatePredictedSales(Double avgDailySales, Integer recentSales) {
        if (avgDailySales == null || avgDailySales == 0) {
            return recentSales != null ? recentSales : 0;
        }
        // Use weighted average: 70% avg, 30% recent
        double predicted = (avgDailySales * 0.7 + (recentSales != null ? recentSales / 30.0 : 0) * 0.3) * 30;
        return (int) Math.ceil(predicted);
    }


    /**
     * Calculate confidence level based on data availability.
     */
    private double calculateConfidence(Long salesRecordCount) {
        if (salesRecordCount == null || salesRecordCount == 0) {
            return 30.0; // Low confidence with no data
        } else if (salesRecordCount < 10) {
            return 50.0 + (salesRecordCount * 2);
        } else if (salesRecordCount < 30) {
            return 70.0 + (salesRecordCount - 10);
        } else {
            return Math.min(95.0, 85.0 + (salesRecordCount - 30) * 0.1);
        }
    }
    /**
     * Calculate risk level.
     */
    private Prevision.NiveauRisque calculateRiskLevel(Integer currentStock, int predictedSales, Integer threshold) {
        if (currentStock == null || currentStock == 0) {
            return Prevision.NiveauRisque.CRITIQUE;
        }

        double daysOfStock = predictedSales > 0 ? (currentStock * 30.0) / predictedSales : 999;

        if (daysOfStock <= 7) {
            return Prevision.NiveauRisque.CRITIQUE;
        } else if (daysOfStock <= 15) {
            return Prevision.NiveauRisque.ELEVE;
        } else if (currentStock <= threshold * 1.5) {
            return Prevision.NiveauRisque.MOYEN;
        } else {
            return Prevision.NiveauRisque.FAIBLE;
        }
    }

    /**
     * Generate recommendation text.
     */
    private String generateRecommendation(String productName, Integer currentStock,
                                          int predictedSales, Integer threshold,
                                          Prevision.NiveauRisque riskLevel) {
        // Try to use AI if available
        if (chatClientBuilder.isPresent()) {
            try {
                return generateAIRecommendation(productName, currentStock, predictedSales, threshold, riskLevel);
            } catch (Exception e) {
                log.warn("AI recommendation failed, using fallback: {}", e.getMessage());
            }
        }

        // Fallback to rule-based recommendation
        return generateFallbackRecommendation(currentStock, predictedSales, threshold, riskLevel);
    }

    /**
     * Generate recommendation using AI.
     */
    private String generateAIRecommendation(String productName, Integer currentStock,
                                            int predictedSales, Integer threshold,
                                            Prevision.NiveauRisque riskLevel) {
        ChatClient chatClient = chatClientBuilder.get().build();

        String prompt = String.format(
                "En tant qu'expert en gestion des stocks, générez une recommandation courte (max 100 mots) pour:\n" +
                        "- Produit: %s\n" +
                        "- Stock actuel: %d unités\n" +
                        "- Ventes prévues 30 jours: %d unités\n" +
                        "- Seuil d'alerte: %d unités\n" +
                        "- Niveau de risque: %s\n" +
                        "Format: Une phrase d'action claire et concise.",
                productName, currentStock, predictedSales, threshold, riskLevel.getLabel()
        );

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }


}
