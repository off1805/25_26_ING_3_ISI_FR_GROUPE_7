package com.projetTransversalIsi.structure_academique.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Classe {
    private Long id;
    private String code;
    private String description;
    private Specialite specialite;

    public Classe(String code, String description, Specialite specialite) {
        this.code = code;
        this.description = description;
        this.specialite = specialite;
    }

    public void update(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
