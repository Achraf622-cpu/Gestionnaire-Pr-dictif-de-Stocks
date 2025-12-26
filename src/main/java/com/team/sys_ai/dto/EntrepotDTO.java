package com.team.sys_ai.dto;

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

    private Long id;

    @NotBlank(message = "Le nom de l'entrepôt est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;

    @NotBlank(message = "La ville est obligatoire")
    @Size(max = 100, message = "La ville ne peut pas dépasser 100 caractères")
    private String ville;

    @Size(max = 255, message = "L'adresse ne peut pas dépasser 255 caractères")
    private String adresse;

    @Size(max = 10, message = "Le code postal ne peut pas dépasser 10 caractères")
    private String codePostal;

    private Boolean actif;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Statistics (optional)
    private Integer nombreProduits;
    private Integer nombreAlertes;
}
