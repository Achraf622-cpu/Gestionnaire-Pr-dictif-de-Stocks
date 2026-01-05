package com.team.sys_ai.repository;

import com.team.sys_ai.entity.Produit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {

        List<Produit> findByActifTrue();

        // Paginated version
        Page<Produit> findByActifTrue(Pageable pageable);

        List<Produit> findByCategorie(String categorie);

        List<Produit> findByCategorieAndActifTrue(String categorie);

        // Paginated version
        Page<Produit> findByCategorieAndActifTrue(String categorie, Pageable pageable);

        List<Produit> findByNomContainingIgnoreCase(String nom);

        // Paginated version
        Page<Produit> findByNomContainingIgnoreCase(String nom, Pageable pageable);

        Optional<Produit> findByNomIgnoreCase(String nom);

        boolean existsByNomIgnoreCase(String nom);

        @Query("SELECT DISTINCT p.categorie FROM Produit p WHERE p.categorie IS NOT NULL ORDER BY p.categorie")
        List<String> findAllCategories();

        @Query("SELECT p FROM Produit p WHERE " +
                        "(:nom IS NULL OR LOWER(p.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) AND " +
                        "(:categorie IS NULL OR p.categorie = :categorie) AND " +
                        "(:actif IS NULL OR p.actif = :actif)")
        List<Produit> searchProducts(
                        @Param("nom") String nom,
                        @Param("categorie") String categorie,
                        @Param("actif") Boolean actif);

        // Paginated search
        @Query("SELECT p FROM Produit p WHERE " +
                        "(:nom IS NULL OR LOWER(p.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) AND " +
                        "(:categorie IS NULL OR p.categorie = :categorie) AND " +
                        "(:actif IS NULL OR p.actif = :actif)")
        Page<Produit> searchProducts(
                        @Param("nom") String nom,
                        @Param("categorie") String categorie,
                        @Param("actif") Boolean actif,
                        Pageable pageable);

        @Query("SELECT p FROM Produit p WHERE p.actif = true ORDER BY p.nom")
        List<Produit> findAllActiveOrderByNom();
}
