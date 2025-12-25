package com.team.sys_ai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Entity
@Table(name = "historique_ventes", indexes = {
        @Index(name = "idx_historique_entrepot", columnList = "entrepot_id"),
        @Index(name = "idx_historique_produit", columnList = "produit_id"),
        @Index(name = "idx_historique_date", columnList = "date_vente"),
        @Index(name = "idx_historique_mois_annee", columnList = "mois, annee")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoriqueVente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le produit est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @NotNull(message = "L'entrepôt est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrepot_id", nullable = false)
    private Entrepot entrepot;

    @NotNull(message = "La date de vente est obligatoire")
    @Column(name = "date_vente", nullable = false)
    private LocalDate dateVente;

    @NotNull(message = "La quantité vendue est obligatoire")
    @Min(value = 1, message = "La quantité vendue doit être positive")
    @Column(name = "quantite_vendue", nullable = false)
    private Integer quantiteVendue;

    @Enumerated(EnumType.STRING)
    @Column(name = "jour_semaine", length = 20)
    private DayOfWeek jourSemaine;

    @Column(nullable = false)
    private Integer mois;

    @Column(nullable = false)
    private Integer annee;

    @PrePersist
    @PreUpdate
    protected void calculateDateFields() {
        if (dateVente != null) {
            this.jourSemaine = dateVente.getDayOfWeek();
            this.mois = dateVente.getMonthValue();
            this.annee = dateVente.getYear();
        }
    }
}
