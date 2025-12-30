package com.team.sys_ai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stocks",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_stock_produit_entrepot",
                columnNames = {"produit_id", "entrepot_id"}
        ),
        indexes = {
                @Index(name = "idx_stock_entrepot", columnList = "entrepot_id"),
                @Index(name = "idx_stock_produit", columnList = "produit_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {

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

    @NotNull(message = "La quantité disponible est obligatoire")
    @Min(value = 0, message = "La quantité disponible ne peut pas être négative")
    @Column(name = "quantite_disponible", nullable = false)
    private Integer quantiteDisponible;

    @NotNull(message = "Le seuil d'alerte est obligatoire")
    @Min(value = 0, message = "Le seuil d'alerte ne peut pas être négatif")
    @Column(name = "seuil_alerte", nullable = false)
    private Integer seuilAlerte;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }

    public boolean isAlertLevel() {
        return quantiteDisponible != null && seuilAlerte != null
                && quantiteDisponible <= seuilAlerte;
    }

    public boolean isCritical() {
        return quantiteDisponible != null && seuilAlerte != null
                && quantiteDisponible <= seuilAlerte / 2;
    }
}
