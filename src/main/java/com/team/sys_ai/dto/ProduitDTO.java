package com.team.sys_ai.dto;

import com.team.sys_ai.entity.Unite;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProduitDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "ID auto-généré du produit")
    private Long id;

    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(max = 150, message = "Le nom ne peut pas dépasser 150 caractères")
    @Schema(description = "Nom du produit", example = "Café Arabica Premium")
    private String nom;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    @Schema(description = "Description détaillée du produit", example = "Café torréfié de haute qualité")
    private String description;

    @Size(max = 100, message = "La catégorie ne peut pas dépasser 100 caractères")
    @Schema(description = "Catégorie du produit", example = "Boissons")
    private String categorie;

    @NotNull(message = "Le prix de vente est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix de vente doit être positif")
    @Schema(description = "Prix de vente TTC", example = "12.99")
    private BigDecimal prixVente;

    @Schema(description = "Poids du produit", example = "0.5")
    private Double poids;

    @Schema(description = "Unité de mesure", example = "KG")
    private Unite unite;

    @Schema(description = "Statut actif du produit", example = "true")
    private Boolean actif;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Date de création")
    private LocalDateTime createdAt;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Date de dernière modification")
    private LocalDateTime updatedAt;
}
