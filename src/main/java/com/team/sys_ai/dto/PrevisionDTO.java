package com.team.sys_ai.dto;

import com.team.sys_ai.entity.Prevision.NiveauRisque;
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
public class PrevisionDTO {

    private Long id;

    private Long produitId;
    private String produitNom;

    private Long entrepotId;
    private String entrepotNom;

    private LocalDate datePrevision;
    private Integer quantitePrevue30Jours;
    private Double niveauConfiance;
    private String recommandation;
    private Integer quantiteRecommandee;
    private NiveauRisque niveauRisque;

    private LocalDateTime createdAt;

    // Additional computed fields
    private Integer stockActuel;
    private Integer seuilAlerte;
}
