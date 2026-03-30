package com.projetTransversalIsi.structure_academique.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Specialite {

    private Long id;
    private String code;
    private String libelle;
    private String description;
    private Niveau niveau;
    private boolean active = true;

    public Specialite(String code, String libelle, String description, Niveau niveau) {
        this.code = code;
        this.libelle = libelle;
        this.description = description;
        this.niveau = niveau;
        this.active = true;
    }

    public void desactiver() {
        this.active = false;
    }

    public void activer() {
        this.active = true;
    }
}
