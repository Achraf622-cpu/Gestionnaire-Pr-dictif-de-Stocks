package com.team.sys_ai.dto;

import com.team.sys_ai.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

    private String token;
    private String type;
    private Long userId;
    private String login;
    private String nom;
    private String prenom;
    private Role role;
    private Long entrepotId;
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
