package com.team.sys_ai.repository;

import com.team.sys_ai.entity.HistoriqueVente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HistoriqueVenteRepository extends JpaRepository<HistoriqueVente, Long> {

        List<HistoriqueVente> findByEntrepotId(Long entrepotId);

        // Paginated version
        Page<HistoriqueVente> findByEntrepotId(Long entrepotId, Pageable pageable);

        List<HistoriqueVente> findByProduitId(Long produitId);

        List<HistoriqueVente> findByProduitIdAndEntrepotId(Long produitId, Long entrepotId);

        // Paginated version
        Page<HistoriqueVente> findByProduitIdAndEntrepotId(Long produitId, Long entrepotId, Pageable pageable);

        List<HistoriqueVente> findByDateVenteBetween(LocalDate startDate, LocalDate endDate);

        List<HistoriqueVente> findByEntrepotIdAndDateVenteBetween(Long entrepotId, LocalDate startDate,
                        LocalDate endDate);

        // Paginated version
        Page<HistoriqueVente> findByEntrepotIdAndDateVenteBetween(Long entrepotId, LocalDate startDate,
                        LocalDate endDate, Pageable pageable);

        List<HistoriqueVente> findByProduitIdAndEntrepotIdAndDateVenteBetween(
                        Long produitId, Long entrepotId, LocalDate startDate, LocalDate endDate);

        @Query("SELECT COALESCE(SUM(h.quantiteVendue), 0) FROM HistoriqueVente h " +
                        "WHERE h.produit.id = :produitId AND h.entrepot.id = :entrepotId " +
                        "AND h.dateVente BETWEEN :startDate AND :endDate")
        Integer getTotalQuantitySold(
                        @Param("produitId") Long produitId,
                        @Param("entrepotId") Long entrepotId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT COALESCE(AVG(h.quantiteVendue), 0) FROM HistoriqueVente h " +
                        "WHERE h.produit.id = :produitId AND h.entrepot.id = :entrepotId " +
                        "AND h.dateVente >= :startDate")
        Double getAverageDailySales(
                        @Param("produitId") Long produitId,
                        @Param("entrepotId") Long entrepotId,
                        @Param("startDate") LocalDate startDate);

        @Query("SELECT h.jourSemaine, SUM(h.quantiteVendue) FROM HistoriqueVente h " +
                        "WHERE h.produit.id = :produitId AND h.entrepot.id = :entrepotId " +
                        "GROUP BY h.jourSemaine")
        List<Object[]> getSalesByDayOfWeek(
                        @Param("produitId") Long produitId,
                        @Param("entrepotId") Long entrepotId);

        @Query("SELECT h.annee, h.mois, SUM(h.quantiteVendue) FROM HistoriqueVente h " +
                        "WHERE h.produit.id = :produitId AND h.entrepot.id = :entrepotId " +
                        "GROUP BY h.annee, h.mois " +
                        "ORDER BY h.annee DESC, h.mois DESC")
        List<Object[]> getMonthlySales(
                        @Param("produitId") Long produitId,
                        @Param("entrepotId") Long entrepotId);

        @Query("SELECT h FROM HistoriqueVente h " +
                        "WHERE h.produit.id = :produitId AND h.entrepot.id = :entrepotId " +
                        "ORDER BY h.dateVente DESC")
        List<HistoriqueVente> findRecentSales(
                        @Param("produitId") Long produitId,
                        @Param("entrepotId") Long entrepotId,
                        Pageable pageable);

        @Query("SELECT h.produit.id, h.produit.nom, SUM(h.quantiteVendue) as totalSold " +
                        "FROM HistoriqueVente h " +
                        "WHERE h.entrepot.id = :entrepotId AND h.dateVente >= :since " +
                        "GROUP BY h.produit.id, h.produit.nom " +
                        "ORDER BY totalSold DESC")
        List<Object[]> getTopSellingProducts(
                        @Param("entrepotId") Long entrepotId,
                        @Param("since") LocalDate since,
                        Pageable pageable);

        @Query("SELECT COUNT(h) FROM HistoriqueVente h " +
                        "WHERE h.produit.id = :produitId AND h.entrepot.id = :entrepotId " +
                        "AND h.dateVente >= :startDate")
        Long countSalesRecords(
                        @Param("produitId") Long produitId,
                        @Param("entrepotId") Long entrepotId,
                        @Param("startDate") LocalDate startDate);
}
