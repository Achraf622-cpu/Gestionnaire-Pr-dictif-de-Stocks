package com.team.sys_ai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "previsions", indexes = {
        @Index(name = "idx_prevision_entrepot", columnList = "entrepot_id"),
        @Index(name = "idx_prevision_produit", columnList = "produit_id"),
        @Index(name = "idx_prevision_date", columnList = "date_prevision")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prevision {

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

    @NotNull(message = "La date de prévision est obligatoire")
    @Column(name = "date_prevision", nullable = false)
    private LocalDate datePrevision;

    @NotNull(message = "La quantité prévue est obligatoire")
    @Min(value = 0, message = "La quantité prévue ne peut pas être négative")
    @Column(name = "quantite_prevue_30_jours", nullable = false)
    private Integer quantitePrevue30Jours;

    @NotNull(message = "Le niveau de confiance est obligatoire")
    @DecimalMin(value = "0.0", message = "Le niveau de confiance doit être entre 0 et 100")
    @DecimalMax(value = "100.0", message = "Le niveau de confiance doit être entre 0 et 100")
    @Column(name = "niveau_confiance", nullable = false)
    private Double niveauConfiance;

    @NotBlank(message = "La recommandation est obligatoire")
    @Size(max = 500, message = "La recommandation ne peut pas dépasser 500 caractères")
    @Column(nullable = false, length = 500)
    private String recommandation;

    @Column(name = "quantite_recommandee")
    private Integer quantiteRecommandee;

    @Enumerated(EnumType.STRING)
    @Column(name = "niveau_risque", length = 20)
    @Builder.Default
    private NiveauRisque niveauRisque = NiveauRisque.FAIBLE;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.datePrevision == null) {
            this.datePrevision = LocalDate.now();
        }
    }

}
