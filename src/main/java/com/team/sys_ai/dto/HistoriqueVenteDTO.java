package com.team.sys_ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueVenteDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "ID auto-généré de la vente")
    private Long id;

    @NotNull(message = "L'ID du produit est obligatoire")
    @Schema(description = "ID du produit vendu", example = "1")
    private Long produitId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Nom du produit")
    private String produitNom;

    @NotNull(message = "L'ID de l'entrepôt est obligatoire")
    @Schema(description = "ID de l'entrepôt source", example = "1")
    private Long entrepotId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Nom de l'entrepôt")
    private String entrepotNom;

    @NotNull(message = "La date de vente est obligatoire")
    @Schema(description = "Date de la vente", example = "2025-01-15")
    private LocalDate dateVente;

    @NotNull(message = "La quantité vendue est obligatoire")
    @Min(value = 1, message = "La quantité vendue doit être positive")
    @Schema(description = "Quantité vendue", example = "25")
    private Integer quantiteVendue;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Jour de la semaine")
    private DayOfWeek jourSemaine;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Mois de la vente")
    private Integer mois;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Année de la vente")
    private Integer annee;
}
