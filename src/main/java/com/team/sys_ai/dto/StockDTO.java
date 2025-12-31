package com.team.sys_ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "ID auto-généré du stock")
    private Long id;

    @NotNull(message = "L'ID du produit est obligatoire")
    @Schema(description = "ID du produit", example = "1")
    private Long produitId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Nom du produit")
    private String produitNom;

    @NotNull(message = "L'ID de l'entrepôt est obligatoire")
    @Schema(description = "ID de l'entrepôt", example = "1")
    private Long entrepotId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Nom de l'entrepôt")
    private String entrepotNom;

    @NotNull(message = "La quantité disponible est obligatoire")
    @Min(value = 0, message = "La quantité disponible ne peut pas être négative")
    @Schema(description = "Quantité en stock", example = "150")
    private Integer quantiteDisponible;

    @NotNull(message = "Le seuil d'alerte est obligatoire")
    @Min(value = 0, message = "Le seuil d'alerte ne peut pas être négatif")
    @Schema(description = "Seuil d'alerte de stock bas", example = "20")
    private Integer seuilAlerte;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Date de dernière mise à jour")
    private LocalDateTime lastUpdated;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Stock en dessous du seuil d'alerte")
    private Boolean enAlerte;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Stock en niveau critique")
    private Boolean critique;
}
