package com.team.sys_ai.dto;

import com.team.sys_ai.entity.NiveauRisque;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Prévision générée par l'IA - lecture seule")
public class PrevisionDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "ID de la prévision")
    private Long id;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "ID du produit")
    private Long produitId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Nom du produit")
    private String produitNom;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "ID de l'entrepôt")
    private Long entrepotId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Nom de l'entrepôt")
    private String entrepotNom;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Date de la prévision")
    private LocalDate datePrevision;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Quantité prévue pour les 30 prochains jours")
    private Integer quantitePrevue30Jours;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Niveau de confiance de la prévision (0-1)")
    private Double niveauConfiance;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Recommandation de l'IA")
    private String recommandation;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Quantité recommandée à commander")
    private Integer quantiteRecommandee;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Niveau de risque de rupture")
    private NiveauRisque niveauRisque;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Date de création")
    private LocalDateTime createdAt;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Stock actuel")
    private Integer stockActuel;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Seuil d'alerte configuré")
    private Integer seuilAlerte;
}
