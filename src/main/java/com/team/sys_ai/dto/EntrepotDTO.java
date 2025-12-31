package com.team.sys_ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntrepotDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "ID auto-généré de l'entrepôt")
    private Long id;

    @NotBlank(message = "Le nom de l'entrepôt est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    @Schema(description = "Nom de l'entrepôt", example = "Entrepôt Central Paris")
    private String nom;

    @NotBlank(message = "La ville est obligatoire")
    @Size(max = 100, message = "La ville ne peut pas dépasser 100 caractères")
    @Schema(description = "Ville de l'entrepôt", example = "Paris")
    private String ville;

    @Size(max = 255, message = "L'adresse ne peut pas dépasser 255 caractères")
    @Schema(description = "Adresse complète", example = "12 Rue de la Logistique")
    private String adresse;

    @Size(max = 10, message = "Le code postal ne peut pas dépasser 10 caractères")
    @Schema(description = "Code postal", example = "75001")
    private String codePostal;

    @Schema(description = "Statut actif de l'entrepôt", example = "true")
    private Boolean actif;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Date de création")
    private LocalDateTime createdAt;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Date de dernière modification")
    private LocalDateTime updatedAt;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Nombre de produits en stock")
    private Integer nombreProduits;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Nombre d'alertes de stock")
    private Integer nombreAlertes;
}
