package com.team.sys_ai.entity;

public enum Unite {
    KG("Kilogramme"),
    LITRE("Litre"),
    UNITE("Unité");

    private final String label;

    Unite(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
