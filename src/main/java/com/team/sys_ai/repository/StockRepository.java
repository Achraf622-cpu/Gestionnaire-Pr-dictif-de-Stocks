package com.team.sys_ai.repository;

import com.team.sys_ai.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    List<Stock> findByEntrepotId(Long entrepotId);

    List<Stock> findByProduitId(Long produitId);

    Optional<Stock> findByEntrepotIdAndProduitId(Long entrepotId, Long produitId);

    boolean existsByEntrepotIdAndProduitId(Long entrepotId, Long produitId);

    @Query("SELECT s FROM Stock s WHERE s.quantiteDisponible <= s.seuilAlerte")
    List<Stock> findStocksAtAlertLevel();

    @Query("SELECT s FROM Stock s WHERE s.entrepot.id = :entrepotId AND s.quantiteDisponible <= s.seuilAlerte")
    List<Stock> findStocksAtAlertLevelByEntrepot(@Param("entrepotId") Long entrepotId);

    @Query("SELECT s FROM Stock s WHERE s.quantiteDisponible <= s.seuilAlerte / 2")
    List<Stock> findCriticalStocks();

    @Query("SELECT s FROM Stock s WHERE s.entrepot.id = :entrepotId AND s.quantiteDisponible <= s.seuilAlerte / 2")
    List<Stock> findCriticalStocksByEntrepot(@Param("entrepotId") Long entrepotId);

    @Query("SELECT s FROM Stock s WHERE s.quantiteDisponible = 0")
    List<Stock> findOutOfStock();

    @Query("SELECT s FROM Stock s WHERE s.entrepot.id = :entrepotId AND s.quantiteDisponible = 0")
    List<Stock> findOutOfStockByEntrepot(@Param("entrepotId") Long entrepotId);

    @Query("SELECT COALESCE(SUM(s.quantiteDisponible), 0) FROM Stock s WHERE s.produit.id = :produitId")
    Integer getTotalQuantityByProduit(@Param("produitId") Long produitId);

    @Query("SELECT s FROM Stock s " +
            "JOIN FETCH s.produit p " +
            "JOIN FETCH s.entrepot e " +
            "WHERE e.id = :entrepotId AND p.actif = true " +
            "ORDER BY p.nom")
    List<Stock> findByEntrepotIdWithDetails(@Param("entrepotId") Long entrepotId);
}