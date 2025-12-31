package com.team.sys_ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Identifiants de connexion")
public class AuthRequestDTO {

    @NotBlank(message = "Le login est obligatoire")
    @Schema(description = "Nom d'utilisateur", example = "admin")
    private String login;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Schema(description = "Mot de passe", example = "admin123")
    private String password;
}
