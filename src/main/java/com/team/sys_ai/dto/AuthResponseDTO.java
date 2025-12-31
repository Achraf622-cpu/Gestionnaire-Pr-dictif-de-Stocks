package com.team.sys_ai.dto;

import com.team.sys_ai.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Réponse d'authentification avec le token JWT")
public class AuthResponseDTO {

    @Schema(description = "Token JWT à utiliser dans l'en-tête Authorization")
    private String token;

    @Schema(description = "Type de token (toujours 'Bearer')", example = "Bearer")
    private String type;

    @Schema(description = "ID de l'utilisateur connecté")
    private Long userId;

    @Schema(description = "Login de l'utilisateur")
    private String login;

    @Schema(description = "Nom de famille")
    private String nom;

    @Schema(description = "Prénom")
    private String prenom;

    @Schema(description = "Rôle de l'utilisateur")
    private Role role;

    @Schema(description = "ID de l'entrepôt assigné (null pour les admins)")
    private Long entrepotId;

    @Schema(description = "Nom de l'entrepôt assigné")
    private String entrepotNom;

    public static AuthResponseDTO of(String token, Long userId, String login, String nom,
            String prenom, Role role, Long entrepotId, String entrepotNom) {
        return AuthResponseDTO.builder()
                .token(token)
                .type("Bearer")
                .userId(userId)
                .login(login)
                .nom(nom)
                .prenom(prenom)
                .role(role)
                .entrepotId(entrepotId)
                .entrepotNom(entrepotNom)
                .build();
    }
}
