package com.team.sys_ai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produits", indexes = {
        @Index(name = "idx_produit_nom", columnList = "nom"),
        @Index(name = "idx_produit_categorie", columnList = "categorie")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(max = 150, message = "Le nom ne peut pas dépasser 150 caractères")
    @Column(nullable = false)
    private String nom;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    @Column(length = 500)
    private String description;

    @Size(max = 100, message = "La catégorie ne peut pas dépasser 100 caractères")
    private String categorie;

    @NotNull(message = "Le prix de vente est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix de vente doit être positif")
    @Digits(integer = 10, fraction = 2, message = "Le prix de vente doit avoir au maximum 2 décimales")
    @Column(name = "prix_vente", nullable = false, precision = 12, scale = 2)
    private BigDecimal prixVente;

    @Column(name = "prix_achat", length = 512)
    private String prixAchat;

    @Column(name = "marge", length = 512)
    private String marge;

    @DecimalMin(value = "0.0", message = "Le poids doit être positif ou nul")
    private Double poids;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private Unite unite = Unite.UNITE;

    @Column(nullable = false)
    @Builder.Default
    private Boolean actif = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Stock> stocks = new ArrayList<>();

    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<HistoriqueVente> historiqueVentes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void setPrixAchatValue(BigDecimal value) {
        this.prixAchat = value != null ? value.toString() : null;
    }

    public BigDecimal getPrixAchatValue() {
        return prixAchat != null ? new BigDecimal(prixAchat) : null;
    }

    public void setMargeValue(BigDecimal value) {
        this.marge = value != null ? value.toString() : null;
    }

    public BigDecimal getMargeValue() {
        return marge != null ? new BigDecimal(marge) : null;
    }
}
