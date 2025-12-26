package com.team.sys_ai.dto;

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

    private Long id;

    @NotNull(message = "L'ID du produit est obligatoire")
    private Long produitId;
    private String produitNom;

    @NotNull(message = "L'ID de l'entrepôt est obligatoire")
    private Long entrepotId;
    private String entrepotNom;

    @NotNull(message = "La date de vente est obligatoire")
    private LocalDate dateVente;

    @NotNull(message = "La quantité vendue est obligatoire")
    @Min(value = 1, message = "La quantité vendue doit être positive")
    private Integer quantiteVendue;

    private DayOfWeek jourSemaine;
    private Integer mois;
    private Integer annee;
}
