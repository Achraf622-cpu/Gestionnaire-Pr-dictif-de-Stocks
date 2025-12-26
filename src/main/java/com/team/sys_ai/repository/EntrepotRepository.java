package com.team.sys_ai.repository;

import com.team.sys_ai.entity.Entrepot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntrepotRepository extends JpaRepository<Entrepot, Long> {

    List<Entrepot> findByActifTrue();

    List<Entrepot> findByActifFalse();

    List<Entrepot> findByVilleIgnoreCase(String ville);

    Optional<Entrepot> findByNomIgnoreCase(String nom);

    boolean existsByNomIgnoreCase(String nom);

    List<Entrepot> findByNomContainingIgnoreCase(String nom);

    @Query("SELECT e FROM Entrepot e WHERE e.actif = true ORDER BY e.nom")
    List<Entrepot> findAllActiveOrderByNom();
}
