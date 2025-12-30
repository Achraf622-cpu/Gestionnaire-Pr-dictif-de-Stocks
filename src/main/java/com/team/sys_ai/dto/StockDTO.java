package com.team.sys_ai.dto;

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

    private Long id;

    @NotNull(message = "L'ID du produit est obligatoire")
    private Long produitId;
    private String produitNom;

    @NotNull(message = "L'ID de l'entrepôt est obligatoire")
    private Long entrepotId;
    private String entrepotNom;

    @NotNull(message = "La quantité disponible est obligatoire")
    @Min(value = 0, message = "La quantité disponible ne peut pas être négative")
    private Integer quantiteDisponible;

    @NotNull(message = "Le seuil d'alerte est obligatoire")
    @Min(value = 0, message = "Le seuil d'alerte ne peut pas être négatif")
    private Integer seuilAlerte;

    private LocalDateTime lastUpdated;


    private Boolean enAlerte;
    private Boolean critique;
}
