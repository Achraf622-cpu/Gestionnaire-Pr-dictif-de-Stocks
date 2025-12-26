package com.team.sys_ai.dto;

import com.team.sys_ai.entity.Unite;
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
public class ProduitAdminDTO {

    private Long id;

    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(max = 150, message = "Le nom ne peut pas dépasser 150 caractères")
    private String nom;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;

    @Size(max = 100, message = "La catégorie ne peut pas dépasser 100 caractères")
    private String categorie;

    @NotNull(message = "Le prix de vente est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix de vente doit être positif")
    private BigDecimal prixVente;

    /**
     * Purchase price - ADMIN only
     */
    @DecimalMin(value = "0.0", message = "Le prix d'achat ne peut pas être négatif")
    private BigDecimal prixAchat;

    /**
     * Margin - ADMIN only
     */
    private BigDecimal marge;

    private Double poids;
    private Unite unite;
    private Boolean actif;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
