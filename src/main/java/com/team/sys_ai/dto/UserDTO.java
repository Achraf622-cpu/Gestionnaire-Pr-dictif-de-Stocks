package com.team.sys_ai.dto;

import com.team.sys_ai.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class UserDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "ID auto-généré de l'utilisateur")
    private Long id;

    @NotBlank(message = "Le login est obligatoire")
    @Size(min = 3, max = 50, message = "Le login doit contenir entre 3 et 50 caractères")
    @Schema(description = "Nom d'utilisateur", example = "jean.dupont")
    private String login;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    @Schema(description = "Nom de famille", example = "Dupont")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères")
    @Schema(description = "Prénom", example = "Jean")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @Schema(description = "Adresse email", example = "jean.dupont@exemple.com")
    private String email;

    @NotNull(message = "Le rôle est obligatoire")
    @Schema(description = "Rôle de l'utilisateur", example = "GESTIONNAIRE")
    private Role role;

    @Schema(description = "Statut actif du compte", example = "true")
    private Boolean actif;

    @Schema(description = "ID de l'entrepôt assigné (pour les gestionnaires)", example = "1")
    private Long entrepotAssigneId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Nom de l'entrepôt assigné")
    private String entrepotAssigneNom;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Date de création du compte")
    private LocalDateTime createdAt;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Date de dernière connexion")
    private LocalDateTime lastLogin;
}
