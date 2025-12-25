package com.team.sys_ai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "entrepots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Entrepot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de l'entrepôt est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    @Column(nullable = false)
    private String nom;

    @NotBlank(message = "La ville est obligatoire")
    @Size(max = 100, message = "La ville ne peut pas dépasser 100 caractères")
    @Column(nullable = false)
    private String ville;

    @Size(max = 255, message = "L'adresse ne peut pas dépasser 255 caractères")
    private String adresse;

    @Size(max = 10, message = "Le code postal ne peut pas dépasser 10 caractères")
    @Column(name = "code_postal")
    private String codePostal;

    @Column(nullable = false)
    @Builder.Default
    private Boolean actif = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "entrepot", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Stock> stocks = new ArrayList<>();

    @OneToMany(mappedBy = "entrepotAssigne")
    @Builder.Default
    private List<User> gestionnaires = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
