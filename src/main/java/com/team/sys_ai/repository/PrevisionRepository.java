package com.team.sys_ai.repository;

import com.team.sys_ai.entity.NiveauRisque;
import com.team.sys_ai.entity.Prevision;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrevisionRepository extends JpaRepository<Prevision, Long> {

    List<Prevision> findByEntrepotId(Long entrepotId);

    List<Prevision> findByProduitId(Long produitId);

    List<Prevision> findByProduitIdAndEntrepotId(Long produitId, Long entrepotId);

    @Query("SELECT p FROM Prevision p " +
            "WHERE p.produit.id = :produitId AND p.entrepot.id = :entrepotId " +
            "ORDER BY p.datePrevision DESC, p.createdAt DESC")
    List<Prevision> findLatestPrediction(
            @Param("produitId") Long produitId,
            @Param("entrepotId") Long entrepotId,
            Pageable pageable
    );

    default Optional<Prevision> findLatestByProduitIdAndEntrepotId(Long produitId, Long entrepotId) {
        List<Prevision> results = findLatestPrediction(produitId, entrepotId, Pageable.ofSize(1));
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    List<Prevision> findByDatePrevision(LocalDate datePrevision);

    @Query("SELECT p FROM Prevision p " +
            "WHERE p.entrepot.id = :entrepotId AND p.niveauRisque = :niveauRisque " +
            "ORDER BY p.datePrevision DESC")
    List<Prevision> findByEntrepotIdAndNiveauRisque(
            @Param("entrepotId") Long entrepotId,
            @Param("niveauRisque") NiveauRisque niveauRisque
    );

    @Query("SELECT p FROM Prevision p " +
            "WHERE p.entrepot.id = :entrepotId " +
            "AND p.niveauRisque IN ('ELEVE', 'CRITIQUE') " +
            "ORDER BY p.niveauRisque DESC, p.datePrevision DESC")
    List<Prevision> findHighRiskPredictions(@Param("entrepotId") Long entrepotId);

    @Query("SELECT p FROM Prevision p " +
            "WHERE p.niveauRisque IN ('ELEVE', 'CRITIQUE') " +
            "ORDER BY p.niveauRisque DESC, p.datePrevision DESC")
    List<Prevision> findAllHighRiskPredictions();

    @Query("SELECT p FROM Prevision p " +
            "WHERE p.entrepot.id = :entrepotId " +
            "ORDER BY p.createdAt DESC")
    List<Prevision> findRecentPredictions(
            @Param("entrepotId") Long entrepotId,
            Pageable pageable
    );

    @Query("DELETE FROM Prevision p WHERE p.datePrevision < :cutoffDate")
    void deleteOldPredictions(@Param("cutoffDate") LocalDate cutoffDate);
}
