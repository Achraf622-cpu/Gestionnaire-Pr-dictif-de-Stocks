package com.team.sys_ai.entity;

public enum NiveauRisque {
    FAIBLE("Faible"),
    MOYEN("Moyen"),
    ELEVE("Élevé"),
    CRITIQUE("Critique");

    private final String label;

    NiveauRisque(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}